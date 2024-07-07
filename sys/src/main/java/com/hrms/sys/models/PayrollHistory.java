package com.hrms.sys.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payrollHistory")
@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayrollHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "year", nullable = false)
    private int year;

    @Column(name = "month", nullable = false)
    private int month;

    @Column(name = "basic_salary_received")
    private Float basicSalaryReceived;

    @Column(name = "overtime_salary")
    private Float overtimeSalary;

    @Column(name = "benefit")
    private Float benefit;

    @Column(name = "gross")
    private Float gross;

    @Column(name = "basicSalary")
    private Float basicSalary;

    @Column(name = "insurance")
    private Float insurance;

    @Column(name = "tax")
    private Float tax;

    @Column(name = "total_salary")
    private Float totalSalary;

    @Column(name = "permitted_leave")
    private Float permittedLeave;

    @Column(name = "total_hours_worked")
    private Float totalHoursWorked;

    @Column(name = "total_hours_overtime")
    private Float totalHoursOvertime;

    private LocalDateTime date;
}
