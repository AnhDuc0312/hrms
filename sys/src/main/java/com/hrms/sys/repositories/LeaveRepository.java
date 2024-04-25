package com.hrms.sys.repositories;

import com.hrms.sys.models.Leave;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveRepository extends JpaRepository<Leave , Long> {
    List<Leave> findByUserId(Long userId);
    List<Leave> findByStatus(String status);

    List<Leave> findAllByOrderByFromDatetimeAsc();

}
