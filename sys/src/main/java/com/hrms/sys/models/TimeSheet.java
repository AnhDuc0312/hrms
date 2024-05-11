package com.hrms.sys.models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "time_sheets")
@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeSheet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "in_time")
    private LocalDateTime inTime;

    @Column(name = "out_time")
    private LocalDateTime outTime;

    @Column(name = "status")
    private String status;

    @Column(name = "working_hours")
    private float workingHours;

    @Column(name = "overtime_hours")
    private float overtimeHours;

    @Column(name = "leave_hours")
    private float leaveHours;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "record_date")
    private LocalDate recordDate;

    @Column(name = "check_in")
    private LocalDateTime checkIn;

    @Column(name = "check_out")
    private LocalDateTime checkOut;

    @Column(name = "type_work")
    private String typeWork;

    @Column(name = "code")
    private String code;


    // Tính số giờ làm việc dựa trên giờ vào và ra
    public void calculateWorkingHours() {
        if (inTime != null && outTime != null) {
            workingHours = (int) Math.abs(outTime.getHour() - inTime.getHour());
        }
    }

    // Tính số giờ làm thêm dựa trên số giờ làm việc và giờ quy định
    public void calculateOvertimeHours(int standardWorkingHours) {
        if (workingHours > standardWorkingHours) {
            overtimeHours = workingHours - standardWorkingHours;
        }
    }

    // Tính số giờ nghỉ dựa trên số giờ làm việc và số giờ quy định
    public void calculateLeaveHours(int standardWorkingHours) {
        if (workingHours < standardWorkingHours) {
            leaveHours = standardWorkingHours - workingHours;
        }
    }
}
