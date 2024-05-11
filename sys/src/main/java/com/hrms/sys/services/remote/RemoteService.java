package com.hrms.sys.services.remote;

import com.hrms.sys.dtos.RemoteDTO;
import com.hrms.sys.models.Employee;
import com.hrms.sys.models.Leave;
import com.hrms.sys.models.Remote;
import com.hrms.sys.models.User;
import com.hrms.sys.repositories.EmployeeRepository;
import com.hrms.sys.repositories.RemoteRepository;
import com.hrms.sys.repositories.UserRepository;
import com.hrms.sys.services.remote.IRemoteService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class RemoteService implements IRemoteService {

    private final RemoteRepository remoteRepository;

    private final ModelMapper modelMapper;

    public final UserRepository userRepository;

    public final EmployeeRepository employeeRepository;


    @Override
    public List<Remote> getAllRemotes() throws Exception {
        return remoteRepository.findAll();
    }

    @Override
    public Remote getRemoteById(long id) throws Exception {
        return remoteRepository.findById(id).orElse(null);
    }

    @Override
    public List<Remote> getRemoteByUserId(long userId) throws Exception {
        User user = userRepository.findById(userId);

        return remoteRepository.findByUser(user);
    }

    @Override
    public Remote createRemote(String username , RemoteDTO remoteDTO) throws Exception {

        LocalDateTime currentDate = LocalDateTime.now();

        if (remoteDTO.getFromDatetime().isBefore(currentDate.plusDays(1))) {
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
        LocalDate remoteStartDate = remoteDTO.getFromDatetime().toLocalDate();
        LocalDate remoteEndDate = remoteDTO.getToDatetime().toLocalDate();

        long remoteDays = remoteStartDate.datesUntil(remoteEndDate.plusDays(1)).count();

        if (remainingPaidRemoteDays >= remoteDays) {
            Remote remote = Remote.builder()
                    .fromDatetime(remoteDTO.getFromDatetime())
                    .toDatetime((remoteDTO.getToDatetime()))
                    .reason(remoteDTO.getReason())
                    .comment(remoteDTO.getComment())
                    .evident(remoteDTO.getEvident())
                    .user(user)
//                    .type(remoteDTO.getType())
                    .approver(null)
                    .workedHours(null)
                    .status("Approved")
                    .build();
            remoteRepository.save(remote);

            float updatedRemainingPaidRemoteDays = remainingPaidRemoteDays - remoteDays;
            employee.setRemainingRemoteDays(updatedRemainingPaidRemoteDays);
            employeeRepository.save(employee);

            return remote;
        }else {
            Remote remote = Remote.builder()
                    .fromDatetime(remoteDTO.getFromDatetime())
                    .toDatetime((remoteDTO.getToDatetime()))
                    .reason(remoteDTO.getReason())
                    .comment(remoteDTO.getComment())
                    .evident(remoteDTO.getEvident())
                    .user(user)
//                    .type(remoteDTO.getType())
                    .approver(null)
                    .workedHours(null)
                    .status("Pending")
                    .build();
            remoteRepository.save(remote);
            return remote;
        }
    }

    @Override
    public Remote updateRemote(long id, RemoteDTO remoteDTO) throws Exception {
        Remote existingRemote = remoteRepository.findById(id).orElse(null);
        if (existingRemote == null) {
            return null; // hoặc throw exception tùy theo yêu cầu
        }
        modelMapper.map(remoteDTO, existingRemote); // Cập nhật thông tin từ DTO vào đối tượng Remote hiện tại
        // Cập nhật các trường khác nếu cần thiết
        return remoteRepository.save(existingRemote);
    }

    @Override
    public void deleteRemote(long id) throws Exception {
        remoteRepository.deleteById(id);
    }

    @Override
    public void approveRemote(long id) throws Exception {
        Remote remote = remoteRepository.findById(id).orElse(null);

    }

    @Override
    public void rejectRemote(long id) throws Exception{
        Remote remote = remoteRepository.findById(id).orElse(null);

    }
}
