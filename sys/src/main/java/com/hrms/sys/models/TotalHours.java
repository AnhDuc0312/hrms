package com.hrms.sys.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "total_hours")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class TotalHours extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "total_working_hours")
    private float totalWorkingHours;

    @Column(name = "total_overtime_hours")
    private float totalOvertimeHours;

    @Column(name = "total_leave_hours")
    private float totalLeaveHours;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
