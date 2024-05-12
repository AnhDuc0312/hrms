package com.hrms.sys.repositories;

import com.hrms.sys.models.Employee;
import com.hrms.sys.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
//    Optional<Employee> findByUsername(String username);
    Optional<Employee> findByUser(User user);

    Page<Employee> findByDepartmentIdAndFullNameContainingIgnoreCaseAndUser_UsernameContainingIgnoreCase(Long departmentId, String keyword, String username, Pageable pageable);

    Page<Employee> findByDepartmentIdAndFullNameContainingIgnoreCase(Long departmentId, String keyword, Pageable pageable);

    Page<Employee> findByDepartmentIdAndUser_UsernameContainingIgnoreCase(Long departmentId, String username, Pageable pageable);

    Page<Employee> findByFullNameContainingIgnoreCaseAndUser_UsernameContainingIgnoreCase(String keyword, String username, Pageable pageable);

    Page<Employee> findByDepartmentId(Long departmentId, Pageable pageable);

    Page<Employee> findByFullNameContainingIgnoreCase(String keyword, Pageable pageable);

    Page<Employee> findByUser_UsernameContainingIgnoreCase(String username, Pageable pageable);

    @Query("SELECT p FROM Employee p WHERE " +

            "(:departmentId IS NULL OR :departmentId = 0 OR p.department.id = :departmentId) " +
            "AND (:keyword IS NULL OR :keyword = '' OR p.fullName LIKE %:keyword% OR p.email LIKE %:keyword%)")
    Page<Employee> searchEmployees
            (@Param("departmentId") Long departmentId,
             @Param("keyword") String keyword, Pageable pageable);
}

//            "(COALESCE(:departmentId, 0) = 0 OR p.department.id = :departmentId) " +