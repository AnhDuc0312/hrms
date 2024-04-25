package com.hrms.sys.services.overtime;

import com.hrms.sys.dtos.OvertimeDTO;
import com.hrms.sys.models.Overtime;

import java.util.List;

public interface IOvertimeService {
    Overtime createOvertime(OvertimeDTO overtimeDTO , Long id) throws Exception;

    List<Overtime> getAllOvertimesByUserId(Long userId) throws Exception;

    public List<Overtime> getAllOvertimesByEmployeeId(Long employeeId) throws Exception;

    List<Overtime> getAllOvertimes() throws Exception;

    Overtime getOvertimeById(Long id) throws Exception;

    void deleteOvertime(Long id) throws Exception;

    Overtime updateOvertime(Long id, OvertimeDTO overtimeDTO) throws Exception;

    void approveOvertime(Long id) throws Exception;

    void rejectOvertime(Long id) throws Exception;
}
