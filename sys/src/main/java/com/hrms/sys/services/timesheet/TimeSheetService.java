package com.hrms.sys.services.timesheet;

import com.hrms.sys.dtos.MonthSummaryDTO;
import com.hrms.sys.dtos.TimeSheetDTO;
import com.hrms.sys.dtos.TotalHoursDTO;
import com.hrms.sys.exceptions.NotFoundException;
import com.hrms.sys.models.TimeSheet;
import com.hrms.sys.models.User;
import com.hrms.sys.repositories.TimeSheetRepository;
import com.hrms.sys.services.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TimeSheetService implements ITimeSheetService{

    private final TimeSheetRepository timeSheetRepository;
    private final UserService userService;
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
        if (hours > 9) {
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
    public TotalHoursDTO getTotalHoursForLastFifteenDays(Long userId) {LocalDate currentDate = LocalDate.now();

        // Calculate start date
        LocalDate previousMonth = currentDate.minusMonths(1);
        int maxDay = previousMonth.lengthOfMonth();
        LocalDate startDate = previousMonth.withDayOfMonth(Math.min(15, maxDay));

        // Calculate end date
        LocalDate endDate = currentDate.withDayOfMonth(15);

        // Lấy danh sách các bản ghi trong khoảng thời gian từ startDate đến endDate
        List<TimeSheet> timeSheets = timeSheetRepository.findByRecordDateBetweenAndUserId(startDate, endDate , userId);

        float totalWorkingHours = 0;
        float totalOvertimeHours = 0;
        float totalLeaveHours = 0;

        for (TimeSheet timeSheet : timeSheets) {
            totalWorkingHours += timeSheet.getWorkingHours();
            totalOvertimeHours += timeSheet.getOvertimeHours();
            totalLeaveHours += timeSheet.getLeaveHours();
        }

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
}
