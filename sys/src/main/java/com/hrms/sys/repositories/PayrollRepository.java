package com.hrms.sys.repositories;

import com.hrms.sys.models.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayrollRepository extends JpaRepository<Payroll, Long> {
}
