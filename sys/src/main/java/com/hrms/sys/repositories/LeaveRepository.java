package com.hrms.sys.repositories;

import com.hrms.sys.models.Leave;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface LeaveRepository extends JpaRepository<Leave , Long> {
    List<Leave> findByUserId(Long userId);
    List<Leave> findByStatus(String status);

    List<Leave> findAllByOrderByFromDatetimeAsc();

    @Query("SELECT l FROM Leave l WHERE l.user.id = :userId AND l.status = 'Approved' AND l.fromDatetime >= :startDate AND l.toDatetime <= :endDate")
    List<Leave> findApprovedLeavesByUserIdAndDateRange(@Param("userId") Long userId,
                                                       @Param("startDate") LocalDateTime startDate,
                                                       @Param("endDate") LocalDateTime endDate);

}
