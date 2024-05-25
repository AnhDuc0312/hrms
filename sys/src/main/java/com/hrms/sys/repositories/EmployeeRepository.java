package com.hrms.sys.repositories;

import com.hrms.sys.models.Employee;
import com.hrms.sys.models.Payroll;
import com.hrms.sys.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
//    Optional<Employee> findByUsername(String username);
    Optional<Employee> findByUser(User user);

    Employee findByPayroll(Payroll payroll);

    Page<Employee> findByDepartmentIdAndFullNameContainingIgnoreCaseAndUser_UsernameContainingIgnoreCase(Long departmentId, String keyword, String username, Pageable pageable);

    Page<Employee> findByDepartmentIdAndFullNameContainingIgnoreCase(Long departmentId, String keyword, Pageable pageable);

    Page<Employee> findByDepartmentIdAndUser_UsernameContainingIgnoreCase(Long departmentId, String username, Pageable pageable);

    Page<Employee> findByFullNameContainingIgnoreCaseAndUser_UsernameContainingIgnoreCase(String keyword, String username, Pageable pageable);

    Page<Employee> findByDepartmentId(Long departmentId, Pageable pageable);

    Page<Employee> findByFullNameContainingIgnoreCase(String keyword, Pageable pageable);

    Page<Employee> findByUser_UsernameContainingIgnoreCase(String username, Pageable pageable);

    @Query("SELECT p FROM Employee p WHERE " +


            "(:keyword IS NULL OR :keyword = '' OR p.fullName LIKE %:keyword% OR p.email LIKE %:keyword% OR p.user.username LIKE %:keyword%)")
    List<Employee> searchEmployees
            (
             @Param("keyword") String keyword);
}

//            "(COALESCE(:departmentId, 0) = 0 OR p.department.id = :departmentId) " +