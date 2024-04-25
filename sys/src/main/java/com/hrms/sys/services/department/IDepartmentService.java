package com.hrms.sys.services.department;

import com.hrms.sys.dtos.DepartmentDTO;
import com.hrms.sys.models.Department;

import java.util.List;

public interface IDepartmentService {
    List<Department> getAllDepartments() throws Exception;

    Department getDepartmentById(long id) throws Exception;

    Department createDepartment(DepartmentDTO departmentDTO) throws Exception;

    Department updateDepartment(long id, DepartmentDTO departmentDTO) throws Exception;

    void deleteDepartment(long id) throws Exception;
}
