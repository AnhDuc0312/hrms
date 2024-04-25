package com.hrms.sys.services.employee;

import com.hrms.sys.dtos.EmployeeDTO;
import com.hrms.sys.models.Employee;
import com.hrms.sys.responses.EmployeeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IEmployeeService {
    Employee createEmployee(EmployeeDTO employeeDTO, MultipartFile avatar) throws Exception;

    Page<EmployeeResponse> getAllEmployees(PageRequest pageRequest, Long departmentId, String keyword) throws Exception;

    EmployeeResponse getEmployeeById(Long id) throws Exception;

    void deleteEmployee(long id) throws Exception;

    Employee updateEmployee(long id, EmployeeDTO employeeDTO) throws Exception;
}
