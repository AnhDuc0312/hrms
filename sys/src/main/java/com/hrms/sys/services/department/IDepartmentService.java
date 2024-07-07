package com.hrms.sys.services.department;

import com.hrms.sys.dtos.DepartmentDTO;
import com.hrms.sys.dtos.TaskDTO;
import com.hrms.sys.models.Department;
import com.hrms.sys.models.Task;
import com.hrms.sys.responses.DepartmentResponse;
import com.hrms.sys.responses.EmployeeResponse;
import com.hrms.sys.responses.TaskResponse;

import java.util.List;
import java.util.Optional;

public interface IDepartmentService {
    List<DepartmentResponse> getAllDepartments();

    public Department addDepartment(DepartmentDTO department);

    public boolean deleteDepartment(Long id);

    public Optional<TaskResponse> addTask(Long departmentId, TaskDTO taskDTO);

    public boolean deleteTask(Long taskId);

    Optional<Task> updateTask(Long taskId, Task taskDetails);

    List<TaskResponse> getTasksByDepartmentAndEmployee(Long departmentId);

    List<EmployeeResponse> getEmployeesByDepartmentId(Long departmentId);

    Optional<TaskResponse> updateTaskStatus(Long taskId, String status);
}
