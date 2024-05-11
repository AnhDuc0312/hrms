package com.hrms.sys.repositories;

import com.hrms.sys.models.Remote;
import com.hrms.sys.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RemoteRepository extends JpaRepository<Remote, Long> {
    List<Remote> findByUser(User user);
}
