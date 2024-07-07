package com.hrms.sys.repositories;

import com.hrms.sys.models.Department;
import com.hrms.sys.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByDepartment(Department department);
}
