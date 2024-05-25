package com.hrms.sys.repositories;

import com.hrms.sys.controllers.OvertimeController;
import com.hrms.sys.models.Overtime;
import com.hrms.sys.models.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface OvertimeRepository extends JpaRepository<Overtime , Long> {


    List<Overtime> findByStatusAndUser_UsernameAndFromDatetimeAfter(String status, String username, LocalDateTime fromDatetime);

    List<Overtime> findByUser(User user);

    // Phương thức để tìm các yêu cầu nghỉ phép trùng lặp cho một người dùng cụ thể
    @Query("SELECT r FROM Overtime r WHERE r.user = :user " +
            "AND (r.fromDatetime BETWEEN :startDatetime AND :endDatetime " +
            "OR r.toDatetime BETWEEN :startDatetime AND :endDatetime " +
            "OR (:startDatetime BETWEEN r.fromDatetime AND r.toDatetime " +
            "AND :endDatetime BETWEEN r.fromDatetime AND r.toDatetime))")
    List<Overtime> findOverlappingOvertimes(@Param("user") User user,
                                        @Param("startDatetime") LocalDateTime startDatetime,
                                        @Param("endDatetime") LocalDateTime endDatetime);

    // Tìm các đơn nghỉ phép trong quá khứ của một người dùng
    @Query("SELECT r FROM Overtime r WHERE r.user = :user AND r.fromDatetime < :currentDate AND r.fromDatetime > :pastDate")
    List<Overtime> findPastOvertimes(@Param("user") User user, @Param("currentDate") LocalDateTime currentDate, @Param("pastDate") LocalDateTime pastDate);

    @Query("SELECT r FROM Overtime r WHERE r.user = :user AND r.fromDatetime >= :currentDate AND r.fromDatetime < :nextWeek ORDER BY r.fromDatetime DESC")
    List<Overtime> findRecentOvertimes(@Param("user") User user, @Param("currentDate") LocalDateTime currentDate, @Param("nextWeek") LocalDateTime nextWeek);

    @Query("SELECT COUNT(r) FROM Overtime r WHERE r.createAt BETWEEN :startOfDay AND :endOfDay")
    long countAllByCreatedAtBetween(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    long countAllByStatusAndCreateAtBetween(String status, LocalDateTime startOfDay, LocalDateTime endOfDay);
}
