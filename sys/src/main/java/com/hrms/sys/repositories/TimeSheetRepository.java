package com.hrms.sys.repositories;

import com.hrms.sys.models.TimeSheet;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TimeSheetRepository extends JpaRepository<TimeSheet, Long> {


    int countByRecordDate(LocalDate date);
    // Tìm bảng chấm công theo id của người dùng và ngày ghi
    Optional<TimeSheet> findByUserIdAndRecordDate(Long userId, LocalDate recordDate);

    // Tìm danh sách bảng chấm công của một người dùng
    List<TimeSheet> findByUserId(Long userId);
    //Tìm kiếm timesheet của người dùng
    TimeSheet findByCode(String code);

    // Tìm danh sách bảng chấm công theo ngày
    List<TimeSheet> findByRecordDate(LocalDate recordDate);

    // Lấy danh sách time sheet và sắp xếp theo ngày và thời gian
    List<TimeSheet> findAll(Sort sort);

    // Lấy danh sách time sheet theo ngày và sắp xếp theo thời gian check-in
    List<TimeSheet> findByRecordDateOrderByCheckInAsc(LocalDate date);

    // Lấy danh sách time sheet theo userId và sắp xếp theo ngày
    List<TimeSheet> findByUserIdOrderByRecordDateDesc(Long userId);

    List<TimeSheet> findByRecordDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT t FROM TimeSheet t WHERE t.recordDate BETWEEN :startDate AND :endDate AND t.user.id = :userId")
    List<TimeSheet> findByRecordDateBetweenAndUserId(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(t.workingHours), 0) FROM TimeSheet t WHERE t.recordDate BETWEEN :startDate AND :endDate")
    Double getTotalWorkingHours(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
