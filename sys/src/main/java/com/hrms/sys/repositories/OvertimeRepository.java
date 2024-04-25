package com.hrms.sys.repositories;

import com.hrms.sys.controllers.OvertimeController;
import com.hrms.sys.models.Overtime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OvertimeRepository extends JpaRepository<Overtime , Long> {


    List<Overtime> findByStatusAndUser_UsernameAndFromDatetimeAfter(String status, String username, LocalDateTime fromDatetime);

}
