package com.hrms.sys.services.leave;

import com.hrms.sys.dtos.LeaveDTO;
import com.hrms.sys.models.Leave;

import java.util.List;

public interface ILeaveService {
    Leave createLeave(String username, LeaveDTO leaveDTO) throws Exception;

    List<Leave> getAllLeaves() throws Exception;

    Leave getLeaveById(Long id) throws Exception;

    void deleteLeave(Long id) throws Exception;

    Leave approveLeave(Long id) throws Exception;

    Leave rejectLeave(Long id) throws Exception;

    List<Leave> getLeaveByUserId(Long id) throws Exception;

    public List<Leave> getLeavesByDate() throws Exception;
}
