package com.hrms.sys.repositories;

import com.hrms.sys.models.TimeSheet;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TimeSheetRepository extends JpaRepository<TimeSheet, Long> {


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
}
