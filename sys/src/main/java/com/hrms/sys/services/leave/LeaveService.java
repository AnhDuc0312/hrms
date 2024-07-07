package com.hrms.sys.services.leave;

import com.hrms.sys.dtos.LeaveDTO;
import com.hrms.sys.models.*;
import com.hrms.sys.repositories.EmployeeRepository;
import com.hrms.sys.repositories.LeaveRepository;
import com.hrms.sys.repositories.UserRepository;
import com.hrms.sys.services.leave.ILeaveService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@AllArgsConstructor
public class LeaveService implements ILeaveService {
    private final LeaveRepository leaveRepository;
    private final ModelMapper modelMapper;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;

    public Leave createLeave(String username, LeaveDTO leaveDTO ) throws Exception {
        LocalDateTime currentDate = LocalDateTime.now();

        //Check thời gian tạo form
        if (leaveDTO.getFromDatetime().isBefore(currentDate.plusDays(1))) {
            throw new RuntimeException("Leave request must be created at least 1 day in advance.");
        }

        if (leaveDTO.getFromDatetime().isBefore(currentDate.plusDays(1))) {
            throw new RuntimeException("Leave request must be created at least 1 day in advance.");
        }



        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Long userId = user.getId();
        //Lấy thông tin nhân viên
        Employee employee = employeeRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        //tính time nghỉ
//        LocalDate leaveStartDate = leaveDTO.getFromDatetime().toLocalDate();
//        LocalDate leaveEndDate = leaveDTO.getToDatetime().toLocalDate();
        // Kiểm tra xem thời gian bắt đầu và kết thúc có trùng nhau không


        LocalDateTime leaveStartDateTime = leaveDTO.getFromDatetime();
        LocalDateTime leaveEndDateTime = leaveDTO.getToDatetime();
        // Chuyển đổi LocalDateTime sang LocalTime
        LocalTime leaveStartTime = leaveStartDateTime.toLocalTime();
        LocalTime leaveEndTime = leaveEndDateTime.toLocalTime();

        float remainingPaidLeaveDays = employee.getRemainingPaidLeaveDays();

        //tính time nghỉ
        LocalDate leaveStartDate = leaveDTO.getFromDatetime().toLocalDate();
        LocalDate leaveEndDate = leaveDTO.getToDatetime().toLocalDate();

        long leaveDays = leaveStartDate.datesUntil(leaveEndDate.plusDays(1)).count();

        if (leaveStartTime.getHour() < 8 || leaveEndTime.getHour() >= 18) {
            throw new RuntimeException("Remote work must start after 8 AM and end before 6 PM.");
        }

        if (leaveStartDateTime.equals(leaveEndDateTime)) {
            throw new RuntimeException("Remote start and end date cannot be the same.");
        }

        // Kiểm tra xem thời gian bắt đầu và kết thúc có trùng với bất kỳ yêu cầu nghỉ phép nào khác không
        List<Leave> overlappingRemotes = leaveRepository.findOverlappingRemotes(user, leaveDTO.getFromDatetime(), leaveDTO.getToDatetime());
        if (!overlappingRemotes.isEmpty()) {
            throw new RuntimeException("The remote request overlaps with existing remote requests.");
        }

        // Tính thời điểm trong tuần tới
        LocalDateTime nextWeek = currentDate.plusDays(7);
//        LocalDateTime nextWeek = currentDate.toLocalDate().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).atStartOfDay();

        // Kiểm tra xem người dùng đã đăng ký remote gần đây nhất cách đây 1 tuần hay không
        List<Leave> recentRemotes = leaveRepository.findRecentRemotes(user, currentDate, nextWeek);
        boolean hasRecentRemote = !recentRemotes.isEmpty();

        long remoteDays = leaveStartDate.datesUntil(leaveEndDate.plusDays(1)).count();

        if (remainingPaidLeaveDays >= leaveDays && !hasRecentRemote && leaveDTO.getType().equals("1")) {
            Leave leave = Leave.builder()
                    .fromDatetime(leaveDTO.getFromDatetime())
                    .toDatetime((leaveDTO.getToDatetime()))
                    .reason(leaveDTO.getReason())
                    .comment(leaveDTO.getComment())
                    .evident(leaveDTO.getEvident())
                    .user(user)
                    .type(leaveDTO.getType())
                    .approver(null)
                    .totalHours(null)
                    .status("Approved")
                    .build();
            leaveRepository.save(leave);

            float updatedRemainingPaidLeaveDays = remainingPaidLeaveDays - leaveDays;
            employee.setRemainingPaidLeaveDays(updatedRemainingPaidLeaveDays);
            employeeRepository.save(employee);

            return leave;
        }else {
            Leave leave = Leave.builder()
                    .fromDatetime(leaveDTO.getFromDatetime())
                    .toDatetime((leaveDTO.getToDatetime()))
                    .reason(leaveDTO.getReason())
                    .comment(leaveDTO.getComment())
                    .evident(leaveDTO.getEvident())
                    .user(employee.getUser())
                    .type(leaveDTO.getType())
                    .approver(null)
                    .totalHours(null)
                    .status("Pending")
                    .build();
            leaveRepository.save(leave);
            return leave;
        }

    }

    public List<Leave> getAllLeaves() throws Exception {
        return leaveRepository.findAll();
    }

    public Leave getLeaveById(Long id) throws Exception {
        return leaveRepository.findById(id).orElse(null);
    }

    public void deleteLeave(Long id) throws Exception {
        leaveRepository.deleteById(id);
    }

    public Leave approveLeave(Long id) throws Exception {
        Leave leave = leaveRepository.findById(id).orElse(null);
        if (leave != null) {
            leave.setStatus("Approved");
            return leaveRepository.save(leave);
        }
        return null;
    }

    public Leave rejectLeave(Long id) throws Exception {
        Leave leave = leaveRepository.findById(id).orElse(null);
        if (leave != null) {
            leave.setStatus("Rejected");
            return leaveRepository.save(leave);
        }
        return null;
    }

    public List<Leave> getLeaveByUserId(Long id) throws Exception {

        return (List<Leave>) leaveRepository.findByUserId(id);
    }

    public List<Leave> getLeavesByDate() throws Exception {
        List<Leave> leaves = leaveRepository.findAllByOrderByFromDatetimeAsc();
        return leaves;
    }

    public long countApprovedLeavesFromStartOfMonthToEndOfMonth(Long userId) {
        // Lấy ngày hiện tại
        LocalDate currentDate = LocalDate.now();

        // Lấy năm và tháng hiện tại
        int year = currentDate.getYear();
        int month = currentDate.getMonthValue() -1 ;

        // Tính ngày đầu tiên của tháng hiện tại
        LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);

        // Tính ngày cuối cùng của tháng hiện tại
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();

        // Tìm danh sách các đơn nghỉ được chấp nhận trong khoảng thời gian từ ngày đầu tiên đến ngày cuối cùng của tháng
        List<Leave> approvedLeaves = leaveRepository.findApprovedLeavesByUserIdAndDateRange(userId, firstDayOfMonth.atStartOfDay(), lastDayOfMonth.atStartOfDay());

        // Trả về số lượng đơn nghỉ được chấp nhận
        return approvedLeaves.size();
    }

    public long countApprovedLeavesForMonthAndYear(long userId, int year, int month) {
        // Tính toán khoảng thời gian bắt đầu và kết thúc của tháng và năm cụ thể
        LocalDateTime startOfMonth = LocalDateTime.of(year, month, 1, 0, 0, 0);
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusDays(1).withHour(23).withMinute(59).withSecond(59);

        // Đếm số lượng đơn nghỉ phép được chấp nhận cho người dùng cụ thể trong khoảng thời gian đã cho
        List<Leave> approvedLeaves = leaveRepository.findApprovedLeavesByUserIdAndDateRange(userId, startOfMonth, endOfMonth);
        return approvedLeaves.size();
    }

    @Override
    public void approveRemote(long id) throws Exception {
        Leave leave = leaveRepository.findById(id).orElse(null);
        leave.setStatus("Approved");
        leave.setTotalHours(8F);

        Employee employee = employeeRepository.findById(leave.getUser().getId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));


        //tính time nghỉ
        LocalDateTime remoteStartDateTime = leave.getFromDatetime();
        LocalDateTime remoteEndDateTime = leave.getToDatetime();
        float remainingPaidLeaveDays = employee.getRemainingPaidLeaveDays();
        long hoursBetween = ChronoUnit.HOURS.between(remoteStartDateTime, remoteEndDateTime);
        float remainingPaidLeaveDaysUpdate = remainingPaidLeaveDays - hoursBetween;
        employee.setRemainingOvertimeHours(remainingPaidLeaveDaysUpdate);
        leaveRepository.save(leave);
        employeeRepository.save(employee);

    }

    @Override
    public void rejectRemote(long id) throws Exception{
        Leave leave = leaveRepository.findById(id).orElse(null);
        assert leave != null;
        if (leave.getStatus().equals("Pending")){
            leave.setStatus("Reject");
            leave.setTotalHours(0F);
            leaveRepository.save(leave);
        }
    }



}
