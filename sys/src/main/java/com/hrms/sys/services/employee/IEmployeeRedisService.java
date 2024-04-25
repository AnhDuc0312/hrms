package com.hrms.sys.services.employee;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hrms.sys.responses.EmployeeResponse;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface IEmployeeRedisService {
    //Clear cached data in Redis
    void clear();//clear cache
    List<EmployeeResponse> getAllEmployees(
            String keyword,
            Long departmentId, PageRequest pageRequest) throws JsonProcessingException;
    void saveAllEmployees(List<EmployeeResponse> employeeResponses,
                         String keyword,
                         Long departmentId,
                         PageRequest pageRequest) throws JsonProcessingException;

}
