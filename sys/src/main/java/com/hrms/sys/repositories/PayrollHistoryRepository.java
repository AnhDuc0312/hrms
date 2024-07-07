package com.hrms.sys.repositories;

import com.hrms.sys.models.Employee;
import com.hrms.sys.models.PayrollHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PayrollHistoryRepository extends JpaRepository<PayrollHistory, Long> {
    Optional<PayrollHistory> findByEmployeeAndYearAndMonth(Employee employee, int year, int month);
    List<PayrollHistory> findByYearAndMonth(int year, int month);
}
