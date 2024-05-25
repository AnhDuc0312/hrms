package com.hrms.sys.services.overtime;

import com.hrms.sys.dtos.OvertimeDTO;
import com.hrms.sys.models.Overtime;

import java.util.List;
import java.util.Map;

public interface IOvertimeService {
    Overtime createOvertime(String username ,OvertimeDTO overtimeDTO ) throws Exception;

    List<Overtime> getAllOvertimesByUserId(Long userId) throws Exception;

    public List<Overtime> getAllOvertimesByEmployeeId(Long employeeId) throws Exception;

    List<Overtime> getAllOvertimes() throws Exception;

    Overtime getOvertimeById(Long id) throws Exception;

    void deleteOvertime(Long id) throws Exception;

    Overtime updateOvertime(Long id, OvertimeDTO overtimeDTO) throws Exception;

    void approveOvertime(String username) throws Exception;

    void rejectOvertime(String username) throws Exception;

    Map<String, Long> getRemoteStatisticsForToday() throws Exception;
}
