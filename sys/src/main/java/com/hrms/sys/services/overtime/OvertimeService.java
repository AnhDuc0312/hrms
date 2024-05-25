package com.hrms.sys.services.overtime;

import com.hrms.sys.dtos.OvertimeDTO;
import com.hrms.sys.models.Employee;
import com.hrms.sys.models.Overtime;
import com.hrms.sys.models.User;
import com.hrms.sys.repositories.EmployeeRepository;
import com.hrms.sys.repositories.OvertimeRepository;
import com.hrms.sys.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@AllArgsConstructor
public class OvertimeService implements IOvertimeService {
    private final OvertimeRepository overtimeRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final EmployeeRepository employeeRepository;

    private static final int MAX_OT_HOURS_PER_MONTH = 30;
    private static final int MAX_OT_HOURS_PER_SESSION = 3;
    private static final int MAX_OT_REQUESTS_PER_WEEK = 2;

    @Override
    public Overtime createOvertime(String username , OvertimeDTO overtimeDTO) throws Exception {
        LocalDateTime currentDate = LocalDateTime.now();

        if (overtimeDTO.getFromDatetime().isBefore(currentDate.plusDays(1))) {
            throw new RuntimeException("Leave request must be created at least 1 day in advance.");
        }



        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Long userId = user.getId();
        //Lấy thông tin nhân viên
        Employee employee = employeeRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        float remainingPaidOvertimeHours = employee.getRemainingOvertimeHours();

        //tính time nghỉ
        LocalDate overtimeStartDate = overtimeDTO.getFromDatetime().toLocalDate();
        LocalDate overtimeEndDate = overtimeDTO.getToDatetime().toLocalDate();

        //Kiem tra thoi gian bat dau va ket thuc
        LocalDateTime overtimeStartDateTime = overtimeDTO.getFromDatetime();
        LocalDateTime overtimeEndDateTime = overtimeDTO.getToDatetime();

        //Chuyen doi sang LocalTime
        LocalTime overtimeStartTime = overtimeStartDateTime.toLocalTime();
        LocalTime overtimeEndTime = overtimeEndDateTime.toLocalTime();

        if (overtimeStartTime.getHour()<8 || overtimeEndTime.getHour() >= 18) {
            throw new RuntimeException("Leave work must start after 8 AM and end before 6 PM");
        }

        if (overtimeStartDateTime.equals(overtimeEndDateTime)){
            throw new RuntimeException("Remote start and end date cannot be the same.");
        }

        // Kiểm tra xem thời gian bắt đầu và kết thúc có trùng với bất kỳ yêu cầu nghỉ phép nào khác không
        List<Overtime> overlappingOvertimes = overtimeRepository.findOverlappingOvertimes(user, overtimeDTO.getFromDatetime(), overtimeDTO.getToDatetime());
        if (!overlappingOvertimes.isEmpty()) {
            throw new RuntimeException("The remote request overlaps with existing remote requests.");
        }

        // Tính thời điểm trong tuần tới
        LocalDateTime nextWeek = currentDate.plusDays(7);
//        LocalDateTime nextWeek = currentDate.toLocalDate().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).atStartOfDay();

        // Kiểm tra xem người dùng đã đăng ký remote gần đây nhất cách đây 1 tuần hay không
        List<Overtime> recentOvertimes = overtimeRepository.findRecentOvertimes(user, currentDate, nextWeek);
        boolean hasRecentRemote = !recentOvertimes.isEmpty();

        Duration duration = Duration.between(overtimeStartDateTime, overtimeEndDateTime);
        float workedHours = (float) duration.toMinutes() / 60;

//        long overtimeDays = overtimeStartDate.datesUntil(overtimeEndDate.plusDays(1)).count();

//        float workedHours = Duration.between(overtimeStartDate, overtimeEndDate).toHours();

        if (remainingPaidOvertimeHours >= workedHours && !hasRecentRemote && overtimeDTO.getType().equals("1")) {
            Overtime overtime = Overtime.builder()
                    .fromDatetime(overtimeDTO.getFromDatetime())
                    .toDatetime((overtimeDTO.getToDatetime()))
                    .reason(overtimeDTO.getReason())
                    .comment(overtimeDTO.getComment())
                    .evident(overtimeDTO.getEvident())
                    .user(user)
                    .type(overtimeDTO.getType())
                    .approver(null)
                    .workedHours(workedHours)
                    .status("Approved")
                    .build();
            overtimeRepository.save(overtime);

            float updatedRemainingPaidOvertimeHours = remainingPaidOvertimeHours - workedHours;
            employee.setRemainingRemoteDays(updatedRemainingPaidOvertimeHours);
            employeeRepository.save(employee);

            return overtime;
        }else {
            Overtime overtime = Overtime.builder()
                    .fromDatetime(overtimeDTO.getFromDatetime())
                    .toDatetime((overtimeDTO.getToDatetime()))
                    .reason(overtimeDTO.getReason())
                    .comment(overtimeDTO.getComment())
                    .evident(overtimeDTO.getEvident())
                    .user(user)
                    .type(overtimeDTO.getType())
                    .approver(null)
                    .workedHours(workedHours)
                    .status("Pending")
                    .build();
            overtimeRepository.save(overtime);
            return overtime;
        }
    }

    @Override
    public List<Overtime> getAllOvertimesByUserId(Long userId) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));

        return overtimeRepository.findByUser(user);
    }

    @Override
    public List<Overtime> getAllOvertimesByEmployeeId(Long employeeId) throws Exception {
        return null;
    }

    @Override
    public List<Overtime> getAllOvertimes() throws Exception {
        List<Overtime> overtimes = overtimeRepository.findAll();
        Collections.reverse(overtimes);
        return overtimes;
    }

    @Override
    public Overtime getOvertimeById(Long id) throws Exception {
        return null;
    }

    @Override
    public void deleteOvertime(Long id) throws Exception {
        overtimeRepository.deleteById(id);

    }

    @Override
    public Overtime updateOvertime(Long id, OvertimeDTO overtimeDTO) throws Exception {
        Overtime existingOvertime = overtimeRepository.findById(id)
                .orElseThrow(() -> new Exception("Overtime not found"));
        modelMapper.map(overtimeDTO, existingOvertime);
        return overtimeRepository.save(existingOvertime);
    }

    @Override
    public void approveOvertime(String username) throws Exception {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Overtime overtime = (Overtime) overtimeRepository.findByUser(user);


        Employee employee = employeeRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // Lấy số giờ làm thêm giờ của đơn overtime
        float workedHours = overtime.getWorkedHours();

        // Cập nhật lại số giờ còn lại cho nhân viên
        float updatedRemainingPaidOvertimeHours = employee.getRemainingOvertimeHours() - workedHours;
        employee.setRemainingOvertimeHours(updatedRemainingPaidOvertimeHours);
        employeeRepository.save(employee);

        // Đặt trạng thái của đơn làm thêm giờ là "Rejected"
        overtime.setStatus("Approved");
        overtimeRepository.save(overtime);
    }

    @Override
    public void rejectOvertime(String username) throws Exception {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Overtime overtime = (Overtime) overtimeRepository.findByUser(user);

        overtime.setStatus("Rejected");
        overtimeRepository.save(overtime);

    }

    // Kiểm tra điều kiện để tự động duyệt đơn OT
    private boolean isAutoApprove(Overtime overtime) {
        LocalDateTime now = LocalDateTime.now();
        if (!overtime.getStatus().equals("Requested")) return false;
        if (overtime.getFromDatetime().isBefore(now.plusDays(1))) return false;
        if (Duration.between(now, overtime.getFromDatetime()).toHours() > 8) return false;
//        if (getTotalOvertimeHoursThisMonth(overtime.getUser().getUsername()) + overtime.getTotalHours() > MAX_OT_HOURS_PER_MONTH) return false;
        return true;
    }

    // Lấy tổng số giờ OT trong tháng của user
    private Float getTotalOvertimeHoursThisMonth(String username) {
        return null;
    }

    @Override
    public Map<String, Long> getRemoteStatisticsForToday() throws Exception {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);

        long totalCount = overtimeRepository.countAllByCreatedAtBetween(startOfDay, endOfDay);
        long pendingCount = overtimeRepository.countAllByStatusAndCreateAtBetween("Pending", startOfDay, endOfDay);

        Map<String, Long> statistics = new HashMap<>();
        statistics.put("total", totalCount);
        statistics.put("pending", pendingCount);

        return statistics;
    }
}