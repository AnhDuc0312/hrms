package com.hrms.sys.services.leave;

import com.hrms.sys.dtos.LeaveDTO;
import com.hrms.sys.models.Employee;
import com.hrms.sys.models.Leave;
import com.hrms.sys.models.User;
import com.hrms.sys.repositories.EmployeeRepository;
import com.hrms.sys.repositories.LeaveRepository;
import com.hrms.sys.repositories.UserRepository;
import com.hrms.sys.services.leave.ILeaveService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Long userId = user.getId();
        //Lấy thông tin nhân viên
        Employee employee = employeeRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        float remainingPaidLeaveDays = employee.getRemainingPaidLeaveDays();

        //tính time nghỉ
        LocalDate leaveStartDate = leaveDTO.getFromDatetime().toLocalDate();
        LocalDate leaveEndDate = leaveDTO.getToDatetime().toLocalDate();

        long leaveDays = leaveStartDate.datesUntil(leaveEndDate.plusDays(1)).count();

        if (remainingPaidLeaveDays >= leaveDays) {
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



}
