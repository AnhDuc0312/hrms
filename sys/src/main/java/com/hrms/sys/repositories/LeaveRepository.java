package com.hrms.sys.repositories;

import com.hrms.sys.models.Leave;
import com.hrms.sys.models.Remote;
import com.hrms.sys.models.User;
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

    // Phương thức để tìm các yêu cầu nghỉ phép trùng lặp cho một người dùng cụ thể
    @Query("SELECT r FROM Leave r WHERE r.user = :user " +
            "AND (r.fromDatetime BETWEEN :startDatetime AND :endDatetime " +
            "OR r.toDatetime BETWEEN :startDatetime AND :endDatetime " +
            "OR (:startDatetime BETWEEN r.fromDatetime AND r.toDatetime " +
            "AND :endDatetime BETWEEN r.fromDatetime AND r.toDatetime))")
    List<Leave> findOverlappingRemotes(@Param("user") User user,
                                        @Param("startDatetime") LocalDateTime startDatetime,
                                        @Param("endDatetime") LocalDateTime endDatetime);

    @Query("SELECT r FROM Leave r WHERE r.user = :user AND r.fromDatetime >= :currentDate AND r.fromDatetime < :nextWeek ORDER BY r.fromDatetime DESC")
    List<Leave> findRecentRemotes(@Param("user") User user, @Param("currentDate") LocalDateTime currentDate, @Param("nextWeek") LocalDateTime nextWeek);

}
