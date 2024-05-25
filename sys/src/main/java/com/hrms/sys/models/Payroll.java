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
public class Payroll extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @OneToOne
//    @JoinColumn(name = "user_id")
//    private User user;

    private LocalDateTime date; //Lấy ra ngày => lấy ra tháng hiện tại

    @Column(name = "basic_salary")
    private Float basicSalary;

    @Column(name = "basic_salary_received")
    private Float basicSalaryReceived;



    @Column(name = "overtime_salary")
    private Float overtimeSalary;

    @Column(name = "benefit")
    private Float benefit;

    @Column(name = "late_deduction")
    private Float lateDeduction;

    @Column (name = "total_salary")
    private Float totalSalary;

    @Column (name = "tax") // thuế
    private Float tax;

    @Column (name = "total_hours_worked")
    private Float totalHoursWorked;

    @Column (name = "total_hours_overtime")
    private Float totalHoursOvertime;

    @Column(name = "salary_overtime")
    private Float salaryOvertime;

    //Thưởng quý
    @Column (name = "quarterly_award")
    private Float quarterlyAward;

    //bhxh
    @Column(name = "insurance")
    private Float insurance;

    //nghỉ có phép
    @Column(name = "permitted_leave")
    private Float permittedLeave;




}
