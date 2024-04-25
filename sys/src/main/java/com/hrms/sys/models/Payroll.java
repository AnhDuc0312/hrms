package com.hrms.sys.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payroll")
@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payroll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime date; //Lấy ra ngày => lấy ra tháng hiện tại

    @Column(name = "basic_salary")
    private Float basicSalary;

    @Column(name = "overtime_salary")
    private Float overtimeSalary;

    @Column(name = "benefit")
    private Float benefit;

    @Column(name = "late_deduction")
    private Float lateDeduction;

    @Column (name = "total_salary")
    private Float totalSalary;


}
