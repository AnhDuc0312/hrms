package com.hrms.sys.controllers;

import com.hrms.sys.dtos.DepartmentDTO;
import com.hrms.sys.dtos.TaskDTO;
import com.hrms.sys.models.Department;
import com.hrms.sys.models.Task;
import com.hrms.sys.responses.DepartmentResponse;
import com.hrms.sys.responses.EmployeeResponse;
import com.hrms.sys.responses.TaskResponse;
import com.hrms.sys.services.department.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("${api.prefix}/departments")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentService departmentService;


    @GetMapping
    public List<DepartmentResponse> getAllDepartments() {
        return departmentService.getAllDepartments();
    }

    @PostMapping
    public ResponseEntity<DepartmentDTO> addDepartment(@RequestBody DepartmentDTO department) {
        Department savedDepartment = departmentService.addDepartment(department);
        return ResponseEntity.ok(department);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        if (departmentService.deleteDepartment(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{departmentId}/tasks")
    public ResponseEntity<TaskResponse> addTask(@PathVariable Long departmentId, @RequestBody TaskDTO taskDTO) {
        Optional<TaskResponse> savedTaskResponse = departmentService.addTask(departmentId, taskDTO);
        return savedTaskResponse.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/tasks/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        if (departmentService.deleteTask(taskId)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/tasks/{taskId}")
    public ResponseEntity<Task> updateTask(@PathVariable Long taskId, @RequestBody Task taskDetails) {
        Optional<Task> updatedTask = departmentService.updateTask(taskId, taskDetails);
        return updatedTask.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/tasks/{taskId}/status")
    public ResponseEntity<TaskResponse> updateTaskStatus(@PathVariable Long taskId, @RequestBody String status) {
        Optional<TaskResponse> updatedTaskResponse = departmentService.updateTaskStatus(taskId, status);
        return updatedTaskResponse.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{departmentId}/tasks")
    public List<TaskResponse> getTasksByDepartmentAndEmployee(@PathVariable Long departmentId) {
        return departmentService.getTasksByDepartmentAndEmployee(departmentId);
    }

    @GetMapping("/{departmentId}/employees")
    public List<EmployeeResponse> getEmployeesByDepartmentId(@PathVariable Long departmentId) {
        return departmentService.getEmployeesByDepartmentId(departmentId);
    }

    @DeleteMapping("/{departmentId}/employees/{username}")
    public ResponseEntity<Void> removeEmployeeFromDepartment(@PathVariable Long departmentId, @PathVariable String username) {
        if (departmentService.removeEmployeeFromDepartment(departmentId, username )) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{departmentId}/employees/{username}")
    public ResponseEntity<Void> addEmployeeToDepartment(@PathVariable Long departmentId, @PathVariable String username ) {
        if (departmentService.addEmployeeToDepartment(departmentId, username)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}
