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

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class RemoteService implements IRemoteService {

    private final RemoteRepository remoteRepository;

    private final ModelMapper modelMapper;

    public final UserRepository userRepository;

    public final EmployeeRepository employeeRepository;



    @Override
    public List<Remote> getAllRemotes() throws Exception {
        List<Remote> remotes = remoteRepository.findAll();
        Collections.reverse(remotes);
        return remotes;
    }

    @Override
    public Remote getRemoteById(long id) throws Exception {
        return remoteRepository.findById(id).orElse(null);
    }

    @Override
    public List<Remote> getRemoteByUserId(long userId) throws Exception {
        User user = userRepository.findById(userId);

        return remoteRepository.findAllByUserOrderByCreateAtDesc(user);
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
        // Kiểm tra xem thời gian bắt đầu và kết thúc có trùng nhau không


        LocalDateTime remoteStartDateTime = remoteDTO.getFromDatetime();
        LocalDateTime remoteEndDateTime = remoteDTO.getToDatetime();
        // Chuyển đổi LocalDateTime sang LocalTime
        LocalTime remoteStartTime = remoteStartDateTime.toLocalTime();
        LocalTime remoteEndTime = remoteEndDateTime.toLocalTime();

        if (remoteStartTime.getHour() < 8 || remoteEndTime.getHour() >= 18) {
            throw new RuntimeException("Remote work must start after 8 AM and end before 6 PM.");
        }

        if (remoteStartDateTime.equals(remoteEndDateTime)) {
            throw new RuntimeException("Remote start and end date cannot be the same.");
        }

        // Kiểm tra xem thời gian bắt đầu và kết thúc có trùng với bất kỳ yêu cầu nghỉ phép nào khác không
        List<Remote> overlappingRemotes = remoteRepository.findOverlappingRemotes(user, remoteDTO.getFromDatetime(), remoteDTO.getToDatetime());
        if (!overlappingRemotes.isEmpty()) {
            throw new RuntimeException("The remote request overlaps with existing remote requests.");
        }

        // Tính thời điểm trong tuần tới
        LocalDateTime nextWeek = currentDate.plusDays(7);
//        LocalDateTime nextWeek = currentDate.toLocalDate().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).atStartOfDay();

        // Kiểm tra xem người dùng đã đăng ký remote gần đây nhất cách đây 1 tuần hay không
        List<Remote> recentRemotes = remoteRepository.findRecentRemotes(user, currentDate, nextWeek);
        boolean hasRecentRemote = !recentRemotes.isEmpty();

        long remoteDays = remoteStartDate.datesUntil(remoteEndDate.plusDays(1)).count();

        // tính số giờ làm việc
        float workedHours = Duration.between(remoteStartTime, remoteEndTime).toHours();


        if (remainingPaidRemoteDays >= remoteDays && !hasRecentRemote && remoteDTO.getType().equals("1")) {
            Remote remote = Remote.builder()
                    .fromDatetime(remoteDTO.getFromDatetime())
                    .toDatetime((remoteDTO.getToDatetime()))
                    .reason(remoteDTO.getReason())
                    .comment(remoteDTO.getComment())
                    .evident(remoteDTO.getEvident())
                    .user(user)
                    .type(remoteDTO.getType())
                    .approver(null)
                    .workedHours(workedHours)
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
                    .type(remoteDTO.getType())
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
        remote.setStatus("Approved");
        remote.setWorkedHours(8F);

        Employee employee = employeeRepository.findById(remote.getUser().getId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));


        //tính time nghỉ
        LocalDate remoteStartDate = remote.getFromDatetime().toLocalDate();
        LocalDate remoteEndDate = remote.getToDatetime().toLocalDate();
        float remainingPaidRemoteDays = employee.getRemainingRemoteDays();
        long remoteDays = remoteStartDate.datesUntil(remoteEndDate.plusDays(1)).count();
        float updatedRemainingPaidRemoteDays = remainingPaidRemoteDays - remoteDays;
        employee.setRemainingRemoteDays(updatedRemainingPaidRemoteDays);
        remoteRepository.save(remote);
        employeeRepository.save(employee);

    }

    @Override
    public void rejectRemote(long id) throws Exception{
        Remote remote = remoteRepository.findById(id).orElse(null);
        assert remote != null;
        if (remote.getStatus().equals("Pending")){
            remote.setStatus("Reject");
            remote.setWorkedHours(0F);
            remoteRepository.save(remote);
        }
    }

    @Override
    public Map<String, Long> getRemoteStatisticsForToday() throws Exception {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);

        long totalCount = remoteRepository.countAllByCreatedAtBetween(startOfDay, endOfDay);
        long pendingCount = remoteRepository.countAllByStatusAndCreateAtBetween("Pending", startOfDay, endOfDay);

        Map<String, Long> statistics = new HashMap<>();
        statistics.put("total", totalCount);
        statistics.put("pending", pendingCount);

        return statistics;
    }
}
