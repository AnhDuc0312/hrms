package com.hrms.sys.repositories;

import com.hrms.sys.models.Employee;
import com.hrms.sys.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User , Long> {
    User findById(long id);

    Optional<User> findByUsername(String username);
//    Optional<User> findByUsername(String username);
//    User existByUsername(String username);

//    User findByEmployee(Employee employee);
    boolean existsByUsername(String username);




}
