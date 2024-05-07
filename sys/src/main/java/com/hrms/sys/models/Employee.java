package com.hrms.sys.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "employees")
@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name")
    private String fullName;

    private String gender;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "address",length = 300)
    private String address;

    @Column(name = "contact_start_date")
    private LocalDate contactStartDate;

    @Column(name = "contact_end_date")
    private LocalDate contactEndDate;

    @Column(name = "avatar_url")
    private String avatarUrl;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String position;

    private String education;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(name = "remaining_paid_leave_days")
    private float remainingPaidLeaveDays;

    @Column(name = "remaining_remote_days")
    private float remainingRemoteDays;

    @Column(name = "remaining_overtime_hours")
    private float remainingOvertimeHours;

    //Lương cơ bản
    @Column(name = "hourly_wage")
    private float hourlyWage;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "benifit")
    private Benefit benifit;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payroll")
    private Payroll payroll;



//    private float

}
