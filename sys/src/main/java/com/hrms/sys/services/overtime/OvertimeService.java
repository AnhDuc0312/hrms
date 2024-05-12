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
import java.util.List;
import java.util.Optional;

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

        float remainingPaidRemoteDays = employee.getRemainingRemoteDays();

        //tính time nghỉ
        LocalDate overtimeStartDate = overtimeDTO.getFromDatetime().toLocalDate();
        LocalDate overtimeEndDate = overtimeDTO.getToDatetime().toLocalDate();

        long remoteDays = overtimeStartDate.datesUntil(overtimeEndDate.plusDays(1)).count();

        if (remainingPaidRemoteDays >= remoteDays) {
            Overtime overtime = Overtime.builder()
                    .fromDatetime(overtimeDTO.getFromDatetime())
                    .toDatetime((overtimeDTO.getToDatetime()))
                    .reason(overtimeDTO.getReason())
                    .comment(overtimeDTO.getComment())
                    .evident(overtimeDTO.getEvident())
                    .user(user)
//                    .type(remoteDTO.getType())
                    .approver(null)
                    .workedHours(null)
                    .status("Approved")
                    .build();
            overtimeRepository.save(overtime);

            float updatedRemainingPaidRemoteDays = remainingPaidRemoteDays - remoteDays;
            employee.setRemainingRemoteDays(updatedRemainingPaidRemoteDays);
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
//                    .type(remoteDTO.getType())
                    .approver(null)
                    .workedHours(null)
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
        return null;
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
    public void approveOvertime(Long id) throws Exception {
        Overtime overtime = overtimeRepository.findById(id)
                .orElseThrow(() -> new Exception("Overtime not found"));
        overtime.setStatus("Approved");
        overtimeRepository.save(overtime);

    }

    @Override
    public void rejectOvertime(Long id) throws Exception {
        Overtime overtime = overtimeRepository.findById(id)
                .orElseThrow(() -> new Exception("Overtime not found"));
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
}