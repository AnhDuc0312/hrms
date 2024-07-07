package com.hrms.sys.services.timesheet;

import com.hrms.sys.dtos.*;
import com.hrms.sys.exceptions.NotFoundException;
import com.hrms.sys.models.TimeSheet;
import com.hrms.sys.models.User;
import com.hrms.sys.repositories.TimeSheetRepository;
import com.hrms.sys.repositories.UserRepository;
import com.hrms.sys.services.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TimeSheetService implements ITimeSheetService{

    private final TimeSheetRepository timeSheetRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    public TimeSheet saveOrUpdateTimeSheet(TimeSheet timeSheet) {
        if (timeSheet.getUser() == null || timeSheet.getRecordDate() == null) {
            // Xử lý khi thông tin bảng chấm công không hợp lệ
            return null;
        }

        Optional<TimeSheet> existingTimeSheet = timeSheetRepository.findByUserIdAndRecordDate(
                timeSheet.getUser().getId(), timeSheet.getRecordDate());

        if (existingTimeSheet.isPresent()) {
            TimeSheet updatedTimeSheet = existingTimeSheet.get();
            updatedTimeSheet.setOutTime(timeSheet.getOutTime());
            // Cập nhật các trường khác nếu cần
            return timeSheetRepository.save(updatedTimeSheet);
        } else {
            // Thêm mới bảng chấm công
            return timeSheetRepository.save(timeSheet);
        }
    }

    // Lấy bảng chấm công theo id
    public TimeSheet getTimeSheetById(Long id) throws NotFoundException {
        Optional<TimeSheet> optionalTimeSheet = timeSheetRepository.findById(id);
        if (optionalTimeSheet.isPresent()) {
            return optionalTimeSheet.get();
        } else {
            // Xử lý trường hợp không tìm thấy bản ghi
            throw new NotFoundException("Time sheet not found with id: " + id);
        }
    }




    public void checkIn(TimeSheetDTO timeSheetDTO) throws NotFoundException {
        User user = userService.getUserById(timeSheetDTO.getUserId());

        String code = LocalDate.now().toString() + user.getUsername();
        TimeSheet timeSheetCheck = timeSheetRepository.findByCode(code);
        if (timeSheetCheck == null ) {
            TimeSheet timeSheet = createTimeSheetFromDTO(timeSheetDTO);
            timeSheetRepository.save(timeSheet);
        } else {
            return ;
        }

//        TimeSheet timeSheet = TimeSheet.builder()
//                .inTime(LocalDateTime.now())
//                .status("Checked-in")
//                // Thêm các trường dữ liệu khác từ timeSheetDTO
//                .build();
//        timeSheetRepository.save(timeSheet);
        return;
    }

    public void checkOut(TimeSheetDTO timeSheetDTO) throws NotFoundException {
        // Tương tự như checkIn, nhưng cập nhật outTime và status
        TimeSheet timeSheet = updateTimeSheetFromDTO(timeSheetDTO);
        calculateWorkHours(timeSheet);
        timeSheetRepository.save(timeSheet);
    }

    public void updateTimeSheet(Long id, TimeSheetDTO timeSheetDTO) throws NotFoundException {
        TimeSheet timeSheet = timeSheetRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Time sheet not found with id: " + id));
        updateFieldsFromDTO(timeSheet, timeSheetDTO);
        calculateWorkHours(timeSheet);
        timeSheetRepository.save(timeSheet);
    }
    private TimeSheet createTimeSheetFromDTO(TimeSheetDTO timeSheetDTO) throws NotFoundException {
        TimeSheet timeSheet = new TimeSheet();
        fillTimeSheetFromDTO(timeSheet, timeSheetDTO);
        timeSheet.setTypeWork(timeSheetDTO.getTypeWork());
        timeSheet.setCheckIn(LocalDateTime.now());
        timeSheet.setInTime(LocalDateTime.now());
        return timeSheet;
    }

    // Phương thức để cập nhật thông tin của TimeSheet từ DTO khi check-out hoặc update
    private TimeSheet updateTimeSheetFromDTO(TimeSheetDTO timeSheetDTO) throws NotFoundException {
        User user = userService.getUserById(timeSheetDTO.getUserId());

        String code = LocalDate.now().toString() + user.getUsername();
        TimeSheet timeSheet = timeSheetRepository.findByCode(code);
//                .orElseThrow(() -> new NotFoundException("Time sheet not found with id: " + timeSheetDTO.getCode()));
        updateFieldsFromDTO(timeSheet, timeSheetDTO);
        timeSheet.setCheckOut(LocalDateTime.now());
        timeSheet.setOutTime(LocalDateTime.now());
        return timeSheet;
    }

    // Phương thức để tính toán số giờ làm việc và cập nhật trạng thái (đủ công hoặc không đủ công)
    private void calculateWorkHours(TimeSheet timeSheet) {
        // Tính toán số giờ làm việc
        Duration duration = Duration.between(timeSheet.getCheckIn(), timeSheet.getCheckOut());
        long hours = duration.toHours();
        if (hours > 8) {
            hours = 8;
        }
        if (timeSheet.getTypeWork().equals("Remote") || timeSheet.getTypeWork().equals("Onsite")) {
            timeSheet.setWorkingHours((float) hours);
        } else if (timeSheet.getTypeWork().equals("Overtime")) {
            timeSheet.setOvertimeHours((float) hours);
        }else {
            timeSheet.setWorkingHours((float) hours);
        }

    }

    // Phương thức để điền thông tin từ DTO vào time sheet
    private void fillTimeSheetFromDTO(TimeSheet timeSheet, TimeSheetDTO timeSheetDTO) throws NotFoundException {
        // Điền thông tin khác từ DTO vào time sheet
//        timeSheet.setInTime(timeSheetDTO.getInTime());
        timeSheet.setOutTime(timeSheetDTO.getOutTime());
        timeSheet.setStatus(timeSheetDTO.getStatus());
        timeSheet.setRecordDate(LocalDate.now());
        timeSheet.setTypeWork(timeSheetDTO.getTypeWork());
        // Lấy thông tin User từ UserService và set vào TimeSheet
        User user = userService.getUserById(timeSheetDTO.getUserId());
        timeSheet.setUser(user);
        timeSheet.setCode(LocalDate.now().toString() + user.getUsername());
    }

    // Phương thức để cập nhật các trường thông tin từ DTO vào time sheet
    private void updateFieldsFromDTO(TimeSheet timeSheet, TimeSheetDTO timeSheetDTO) throws NotFoundException {
        // Cập nhật thông tin từ DTO vào time sheet
        if (timeSheet.getInTime() == null){
            timeSheet.setStatus("Chưa check-in");
        } else {
            timeSheet.setStatus("Đã check out");
        }
    }

    // Service để lấy tất cả time sheet và sắp xếp theo ngày và thời gian
    public List<TimeSheetDTO> getAllTimeSheetsSortedByDateTime() {
        List<TimeSheet> timeSheets = timeSheetRepository.findAll(Sort.by(Sort.Direction.DESC, "recordDate", "checkIn"));
        return mapTimeSheetsToDTOs(timeSheets);
    }

    // Service để lấy time sheet theo ngày và sắp xếp theo thời gian check-in
    public List<TimeSheetDTO> getTimeSheetsByDateAndSortByCheckIn(LocalDate date) {
        List<TimeSheet> timeSheets = timeSheetRepository.findByRecordDateOrderByCheckInAsc(date);
        return mapTimeSheetsToDTOs(timeSheets);
    }
    // Service để lấy time sheet theo userId và sắp xếp theo ngày
    public List<TimeSheetDTO> getTimeSheetsByUserIdAndSortByDate(Long userId) {
        List<TimeSheet> timeSheets = timeSheetRepository.findByUserIdOrderByRecordDateDesc(userId);
        return mapTimeSheetsToDTOs(timeSheets);
    }



    // Hàm chuyển đổi danh sách TimeSheet thành danh sách TimeSheetDTO
    private List<TimeSheetDTO> mapTimeSheetsToDTOs(List<TimeSheet> timeSheets) {
        return timeSheets.stream()
                .map(this::mapTimeSheetToDTO)
                .collect(Collectors.toList());
    }

    // Hàm chuyển đổi TimeSheet thành TimeSheetDTO
    private TimeSheetDTO mapTimeSheetToDTO(TimeSheet timeSheet) {
        TimeSheetDTO timeSheetDTO = new TimeSheetDTO();
        // Ánh xạ các trường thông tin từ timeSheet sang timeSheetDTO
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String formattedInTime = timeSheet.getInTime().format(formatter); // Định dạng thành chuỗi


//        timeSheetDTO.setId(timeSheet.getId());
        timeSheetDTO.setInTime(timeSheet.getInTime());
        timeSheetDTO.setCheckIn(timeSheet.getCheckIn());
        timeSheetDTO.setOutTime(timeSheet.getOutTime());
        timeSheetDTO.setCheckOut(timeSheet.getCheckOut());
        timeSheetDTO.setRecordDate(timeSheet.getRecordDate());
        timeSheetDTO.setUserId(timeSheet.getUser().getId());
        timeSheetDTO.setLeaveHours(timeSheet.getLeaveHours());
        timeSheetDTO.setWorkingHours(timeSheet.getWorkingHours());
        timeSheetDTO.setOvertimeHours(timeSheet.getOvertimeHours());
        timeSheetDTO.setStatus(timeSheet.getStatus());
        timeSheetDTO.setTypeWork(timeSheet.getTypeWork());
        timeSheetDTO.setCode(timeSheet.getCode());
        timeSheetDTO.setFullName(timeSheet.getUser().getFullName());
        return timeSheetDTO;
    }
    public TotalHoursDTO getTotalHoursForMonth(Long userId) {
        // Lấy ngày hiện tại
        LocalDate currentDate = LocalDate.now();

        // Lấy năm và tháng hiện tại
        int year = currentDate.getYear();
        int month = currentDate.getMonthValue() - 1;

        // Tính ngày đầu tiên của tháng hiện tại
        LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);

        // Tính ngày cuối cùng của tháng hiện tại
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();

        // Lấy danh sách các bản ghi trong khoảng thời gian từ ngày đầu tiên đến ngày cuối cùng của tháng
        List<TimeSheet> timeSheets = timeSheetRepository.findByRecordDateBetweenAndUserId(firstDayOfMonth, lastDayOfMonth, userId);

        // Khởi tạo biến tổng số giờ làm việc, số giờ làm thêm và số giờ nghỉ
        float totalWorkingHours = 0;
        float totalOvertimeHours = 0;
        float totalLeaveHours = 0;

        // Tính tổng số giờ làm việc, số giờ làm thêm và số giờ nghỉ từ danh sách bản ghi
        for (TimeSheet timeSheet : timeSheets) {
            totalWorkingHours += timeSheet.getWorkingHours();
            totalOvertimeHours += timeSheet.getOvertimeHours();
            totalLeaveHours += timeSheet.getLeaveHours();
        }

        // Trả về đối tượng TotalHoursDTO
        return new TotalHoursDTO(userId, totalWorkingHours, totalOvertimeHours, totalLeaveHours);
    }


    // Phương thức để lấy số lượng nhân viên đã check-in trong một ngày cụ thể
    public long getCheckedInCountByDate(LocalDate date) {
        return timeSheetRepository.countByRecordDate(date);
    }

    // Phương thức để lấy số ngày làm việc của mỗi nhân viên trong một khoảng thời gian
    public Map<Long, Long> getWorkDaysForEachEmployee(LocalDate startDate, LocalDate endDate) {
        List<TimeSheet> timeSheets = timeSheetRepository.findByRecordDateBetween(startDate, endDate);
        return timeSheets.stream()
                .filter(ts -> ts.getInTime() != null && ts.getOutTime() != null)
                .collect(Collectors.groupingBy(ts -> ts.getUser().getId(), Collectors.counting()));
    }

    // Phương thức để lấy số ngày nghỉ của mỗi nhân viên trong một khoảng thời gian
    public Map<Long, Long> getLeaveDaysForEachEmployee(LocalDate startDate, LocalDate endDate) {
        List<TimeSheet> timeSheets = timeSheetRepository.findByRecordDateBetween(startDate, endDate);
        return timeSheets.stream()
                .filter(ts -> ts.getLeaveHours() > 0)
                .collect(Collectors.groupingBy(ts -> ts.getUser().getId(), Collectors.counting()));
    }

    public List<MonthSummaryDTO> getMonthlyWorkSummaryByUserIdAndYear(Long userId, int year) throws NotFoundException {
        User user = userService.getUserById(userId);
        List<TimeSheet> timeSheets = timeSheetRepository.findByUserId(userId);

        Map<Month, MonthSummaryDTO> monthSummaryMap = new HashMap<>();

        // Initialize month summaries
        for (Month month : Month.values()) {
            monthSummaryMap.put(month, new MonthSummaryDTO(month.toString(), 0, 0));
        }

        // Calculate working days and off days for each month in the specified year
        for (TimeSheet timeSheet : timeSheets) {
            LocalDate recordDate = timeSheet.getRecordDate();
            if (recordDate.getYear() == year) {
                Month month = recordDate.getMonth();
                MonthSummaryDTO summary = monthSummaryMap.get(month);
                if (timeSheet.getLeaveHours() > 0) {
                    summary.setOff(summary.getOff() + 1);
                } else {
                    summary.setWorking(summary.getWorking() + 1);
                }
            }
        }

        return new ArrayList<>(monthSummaryMap.values());
    }


    public TotalHoursDTO getTotalHoursForMonthAndYear(long userId, int year, int month) {
        // Tính toán khoảng thời gian bắt đầu và kết thúc của tháng và năm cụ thể
        LocalDateTime startOfMonth = LocalDateTime.of(year, month, 1, 0, 0, 0);
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusDays(1).withHour(23).withMinute(59).withSecond(59);

        // Tính toán tổng số giờ làm việc từ bảng TimeSheet cho người dùng cụ thể
        List<TimeSheet> timeSheets = timeSheetRepository.findByUserIdAndRecordDateBetween(userId, startOfMonth.toLocalDate(), endOfMonth.toLocalDate());
        float totalWorkingHours = 0;
        float totalOvertimeHours = 0;

        for (TimeSheet timeSheet : timeSheets) {
            totalWorkingHours += timeSheet.getWorkingHours();
            totalOvertimeHours += timeSheet.getOvertimeHours();
        }

        return TotalHoursDTO.builder()
                .userId(userId)
                .totalWorkingHours(totalWorkingHours)
                .totalOvertimeHours(totalOvertimeHours)
                .build();
    }


    public List<MonthlyWorkSummaryDTO> getMonthlyWorkSummaryForAllEmployeesByYear(int year) throws Exception {
        List<TimeSheet> timeSheets = timeSheetRepository.findAll();

        // Map để lưu trữ tổng kết hàng tháng cho từng nhân viên
        Map<Long, Map<Month, MonthlyWorkSummaryDTO>> employeeMonthSummaryMap = new HashMap<>();

        // Khởi tạo tổng kết hàng tháng cho tất cả nhân viên
        List<User> users = userService.getUsers();
        for (User user : users) {
            Map<Month, MonthlyWorkSummaryDTO> monthSummaryMap = new HashMap<>();
            for (Month month : Month.values()) {
                monthSummaryMap.put(month, new MonthlyWorkSummaryDTO(user.getId(), user.getUsername(), month.toString(), 0, 0));
            }
            employeeMonthSummaryMap.put(user.getId(), monthSummaryMap);
        }

        // Tính toán số ngày làm việc và số ngày nghỉ cho từng tháng trong năm cụ thể
        for (TimeSheet timeSheet : timeSheets) {
            LocalDate recordDate = timeSheet.getRecordDate();
            if (recordDate.getYear() == year) {
                Long userId = timeSheet.getUser().getId();
                Month month = recordDate.getMonth();
                MonthlyWorkSummaryDTO summary = employeeMonthSummaryMap.get(userId).get(month);
                if (timeSheet.getLeaveHours() > 0) {
                    summary.setOffDays(summary.getOffDays() + 1);
                } else {
                    summary.setWorkingDays(summary.getWorkingDays() + 1);
                }
            }
        }

        // Tạo danh sách tổng kết hàng tháng cho tất cả nhân viên
        List<MonthlyWorkSummaryDTO> monthlySummaries = new ArrayList<>();
        for (Map<Month, MonthlyWorkSummaryDTO> monthSummaryMap : employeeMonthSummaryMap.values()) {
            monthlySummaries.addAll(monthSummaryMap.values());
        }

        // Sort the summaries by month
        monthlySummaries.sort(Comparator.comparingInt(summary -> Month.valueOf(summary.getMonth().toUpperCase()).getValue()));

        return monthlySummaries;
    }

    // Phương thức để lấy ngày bắt đầu và kết thúc của tuần hiện tại
    public static LocalDate[] getCurrentWeekDates() {
        LocalDate currentDate = LocalDate.now();
        LocalDate startDate = currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endDate = startDate.plusDays(6);
        return new LocalDate[]{startDate, endDate};
    }

    // Phương thức để tính toán dữ liệu chấm công theo tuần, bắt đầu từ ngày hiện tại
    public List<Map<String, Object>> getAttendanceCountByCurrentWeek() {
        LocalDate[] currentWeekDates = getCurrentWeekDates();
        LocalDate startDate = currentWeekDates[0];
        LocalDate endDate = currentWeekDates[1];

        Map<DayOfWeek, Map<String, Long>> attendanceMap = new HashMap<>();
        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            attendanceMap.put(dayOfWeek, new HashMap<>());
        }

        // Lấy dữ liệu chấm công từ cơ sở dữ liệu dựa trên ngày bắt đầu và kết thúc của tuần
        List<TimeSheet> timeSheets = timeSheetRepository.findByRecordDateBetween(startDate, endDate);

        // Tính toán số lần chấm công cho mỗi ngày trong tuần
        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            for (TimeSheet timeSheet : timeSheets) {
                if (timeSheet.getRecordDate().getDayOfWeek() == dayOfWeek) {
                    String attendanceType = timeSheet.getInTime() != null && timeSheet.getOutTime() != null ? "Present" : "Absent";
                    attendanceMap.get(dayOfWeek).merge(attendanceType, 1L, Long::sum);
                }
            }
        }
        List<Map<String, Object>> resultList = convertAttendanceMapToList(attendanceMap);

        return resultList;
    }


    // Phương thức để tính toán dữ liệu chấm công theo tháng
    public List<Map<String, Object>> getAttendanceCountByMonth(int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        Map<Integer, Map<String, Long>> attendanceMap = new HashMap<>();
        for (int i = 1; i <= endDate.lengthOfMonth(); i++) {
            attendanceMap.put(i, new HashMap<>());
        }

        List<TimeSheet> timeSheets = timeSheetRepository.findByRecordDateBetween(startDate, endDate);
        for (int i = 1; i <= endDate.lengthOfMonth(); i++) {
            LocalDate currentDate = LocalDate.of(year, month, i);
            for (TimeSheet timeSheet : timeSheets) {
                if (timeSheet.getRecordDate().isEqual(currentDate)) {
                    String attendanceType = timeSheet.getInTime() != null && timeSheet.getOutTime() != null ? "Present" : "Absent";
                    attendanceMap.get(i).merge(attendanceType, 1L, Long::sum);
                }
            }
        }

        List<Map<String, Object>> resultList = convertAttendanceMapToList(attendanceMap);

        return resultList;
    }


//    public List<Map<String, Object>> convertAttendanceMapToList(Map<DayOfWeek, Map<String, Long>> attendanceMap) {
//        List<Map<String, Object>> resultList = new ArrayList<>();
//
//        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
//            Map<String, Object> dayData = new HashMap<>();
//            dayData.put("day", dayOfWeek.toString());
//
//            Map<String, Long> dayAttendance = attendanceMap.getOrDefault(dayOfWeek, new HashMap<>());
//            dayData.put("working", dayAttendance.getOrDefault("Present", 0L));
//            dayData.put("off", dayAttendance.getOrDefault("Absent", 0L));
//
//            resultList.add(dayData);
//        }
//
//        return resultList;
//    }
// Phương thức chuyển đổi dữ liệu Map sang danh sách các đối tượng Map
private List<Map<String, Object>> convertAttendanceMapToList(Map<?, Map<String, Long>> attendanceMap) {
    List<Map<String, Object>> resultList = new ArrayList<>();

    for (Map.Entry<?, Map<String, Long>> entry : attendanceMap.entrySet()) {
        Map<String, Object> dayData = new HashMap<>();
        dayData.put("day", entry.getKey().toString());
        dayData.put("working", entry.getValue().getOrDefault("Present", 0L));
        dayData.put("off", entry.getValue().getOrDefault("Absent", 0L));
        resultList.add(dayData);
    }

    return resultList;
}
    @Override
    public List<DailyWorkDetailDTO> getMonthlyWorkSummaryByYear(int year, String username) {
        List<TimeSheet> timeSheets = timeSheetRepository.findAll();

        List<DailyWorkDetailDTO> dailyWorkDetails = new ArrayList<>();

        // Tìm người dùng với username
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            // Xử lý khi không tìm thấy người dùng
            return dailyWorkDetails; // hoặc có thể trả về null hoặc throw exception
        }
        User user = userOptional.get();

        // Tạo map để lưu trữ danh sách các ngày làm việc của người dùng trong tháng
        Map<LocalDate, DailyWorkDetailDTO> workDetailMap = new HashMap<>();

        // Lặp qua các bản ghi TimeSheet để tính toán số ngày làm việc và nghỉ trong tháng
        for (TimeSheet timeSheet : timeSheets) {
            LocalDate recordDate = timeSheet.getRecordDate();
            if (recordDate.getYear() == year && timeSheet.getUser().equals(user)) {
                // Kiểm tra nếu ngày này đã có trong map, nếu chưa thì thêm vào
                workDetailMap.computeIfAbsent(recordDate, date -> {
                    DailyWorkDetailDTO detailDTO = new DailyWorkDetailDTO();
                    detailDTO.setUserId(user.getId());
                    detailDTO.setUsername(user.getUsername());
                    detailDTO.setWorkDate(recordDate);
                    return detailDTO;
                });

                DailyWorkDetailDTO detailDTO = workDetailMap.get(recordDate);
                if (timeSheet.getLeaveHours() > 0) {
                    detailDTO.setOffDay(true);
                } else {
                    detailDTO.setWorkingDay(true);
                }
            }
        }

        // Chuyển map thành danh sách kết quả
        dailyWorkDetails.addAll(workDetailMap.values());

        // Sắp xếp theo ngày làm việc
        dailyWorkDetails.sort(Comparator.comparing(DailyWorkDetailDTO::getWorkDate));

        return dailyWorkDetails;
    }

    public void updateCheckInOutTime(String code, TimeSheetDTO timeSheetDTO) throws NotFoundException {
        // Tìm TimeSheet dựa trên code
        TimeSheet timeSheet = timeSheetRepository.findByCode(code);
        if (timeSheet == null) {
            throw new NotFoundException("Time sheet not found with code: " + code);
        }

        // Cập nhật thời gian check-in và check-out từ timeSheetDTO nếu có
        if (timeSheetDTO.getInTime() != null) {
            timeSheet.setInTime(timeSheetDTO.getInTime());
            timeSheet.setCheckIn(timeSheetDTO.getInTime());
        }
        if (timeSheetDTO.getOutTime() != null) {
            timeSheet.setOutTime(timeSheetDTO.getOutTime());
            timeSheet.setCheckOut(timeSheetDTO.getOutTime());
            calculateWorkHours(timeSheet); // Tính toán lại số giờ làm việc nếu cần
        }

        // Lưu lại vào repository
        timeSheetRepository.save(timeSheet);
    }


}
