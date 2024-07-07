package com.hrms.sys.services.department;

import com.hrms.sys.dtos.DepartmentDTO;
import com.hrms.sys.dtos.TaskDTO;
import com.hrms.sys.models.Department;
import com.hrms.sys.models.Employee;
import com.hrms.sys.models.Task;
import com.hrms.sys.models.User;
import com.hrms.sys.repositories.DepartmentRepository;
import com.hrms.sys.repositories.EmployeeRepository;
import com.hrms.sys.repositories.TaskRepository;
import com.hrms.sys.repositories.UserRepository;
import com.hrms.sys.responses.DepartmentResponse;
import com.hrms.sys.responses.EmployeeResponse;
import com.hrms.sys.responses.TaskResponse;
import com.hrms.sys.services.department.IDepartmentService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DepartmentService implements IDepartmentService {

    private final DepartmentRepository departmentRepository;
    private final TaskRepository taskRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;

    public List<DepartmentResponse> getAllDepartments() {
        List<Department> departments =  departmentRepository.findAll();
        List<DepartmentResponse> departmentResponses = new ArrayList<>();
        for (Department department : departments) {
            DepartmentResponse departmentResponse = new DepartmentResponse();
            departmentResponse.setId(department.getId());
            departmentResponse.setName(department.getName());
            departmentResponse.setManagerId(department.getManagerId());
            Optional<User> user = userRepository.findById(Long.valueOf(department.getManagerId()));
            if (user.isPresent()) {
                departmentResponse.setManagerName(user.get().getFullName());
                departmentResponse.setManagerUsername(user.get().getUsername());
            }
            departmentResponses.add(departmentResponse);
        }
        return departmentResponses;
    }

    public Department addDepartment(DepartmentDTO department) {
        Department department1 = new Department();
        department1.setName(department.getName());
        if (department.getManagerId() == null) {
            department1.setManagerId("1");
        }else {
            department1.setManagerId(department.getManagerId());
        }


        return departmentRepository.save(department1);
    }

    public boolean deleteDepartment(Long id) {
        Optional<Department> departmentOptional = departmentRepository.findById(id);
        if (departmentOptional.isPresent()) {
            departmentRepository.delete(departmentOptional.get());
            return true;
        } else {
            return false;
        }
    }

    public Optional<TaskResponse> addTask(Long departmentId, TaskDTO taskDTO) {
        Optional<Department> departmentOptional = departmentRepository.findById(departmentId);
        Optional<User> user = userRepository.findByUsername(taskDTO.getUsername());
        User user_1 = user.get();
        if (user_1.getDepartment() !=null ){
            if (user_1.getDepartment().getId().equals(departmentId)){
                Task task = new Task();
                if (departmentOptional.isPresent()) {
                    task.setDepartment(departmentOptional.get());
                    task.setName(taskDTO.getName());
                    task.setFullname(user.get().getFullName());
                    task.setManagerId(Long.valueOf(departmentOptional.get().getManagerId()));
                    task.setStatus("OPEN");
                    task.setUsername(user.get().getUsername());
                    task.setDescription(taskDTO.getDescription());
                    task.setUserId(user.get().getId());
                    Task savedTask = taskRepository.save(task);
                    TaskResponse taskResponse = TaskResponse.convertToTaskResponse(savedTask);
                    return Optional.of(taskResponse);
                }
            }
        }
        return null;

    }

    public boolean deleteTask(Long taskId) {
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if (taskOptional.isPresent()) {
            taskRepository.delete(taskOptional.get());
            return true;
        } else {
            return false;
        }
    }

    public Optional<Task> updateTask(Long taskId, Task taskDetails) {
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();
            task.setName(taskDetails.getName());
            task.setDescription(taskDetails.getDescription());
            task.setStatus(taskDetails.getStatus());
            taskRepository.save(task);
            return Optional.of(task);
        } else {
            return Optional.empty();
        }
    }

    public List<TaskResponse> getTasksByDepartmentAndEmployee(Long departmentId) {
        Department department = departmentRepository.findById(departmentId).get();
        List<Task> tasks =  taskRepository.findByDepartment(department);
        List<TaskResponse> taskResponses = new ArrayList<>();
        for (Task task : tasks) {
            TaskResponse taskResponse = TaskResponse.convertToTaskResponse(task);
            taskResponses.add(taskResponse);
        }
        return taskResponses;
    }

    public List<EmployeeResponse> getEmployeesByDepartmentId(Long departmentId) {
        List<Employee> employees = employeeRepository.findByDepartmentId(departmentId);

        return employees.stream()
                .map(EmployeeResponse::fromEmployee)
                .collect(Collectors.toList());
    }

    public Optional<TaskResponse> updateTaskStatus(Long taskId, String status) {
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if (!taskOptional.isPresent()) {
            return Optional.empty(); // Task not found
        }

        Task task = taskOptional.get();
        task.setStatus(status);
        Task savedTask = taskRepository.save(task);

        return Optional.of(TaskResponse.convertToTaskResponse(savedTask));
    }

    public boolean removeEmployeeFromDepartment(Long departmentId, String username ) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        User user = userOptional.get();
        Optional<Employee> employeeOptional = employeeRepository.findByUser(user);
        Optional<Department> departmentOptional = departmentRepository.findById(departmentId);

        if (employeeOptional.isPresent() && departmentOptional.isPresent()) {
            Employee employee = employeeOptional.get();
            Department department = departmentOptional.get();

            if (employee.getDepartment().equals(department)) {
                employee.setDepartment(null);
                employeeRepository.save(employee);
                return true;
            }
        }
        return false;
    }

    public boolean addEmployeeToDepartment(Long departmentId, String username ) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        User user = userOptional.get();
        Optional<Employee> employeeOptional = employeeRepository.findByUser(user);
        Optional<Department> departmentOptional = departmentRepository.findById(departmentId);

        if (employeeOptional.isPresent() && departmentOptional.isPresent()) {
            Employee employee = employeeOptional.get();
            Department department = departmentOptional.get();

            employee.setDepartment(department);
            employeeRepository.save(employee);
            return true;
        }
        return false;
    }
}
