package com.hrms.sys.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimeSheetDTO {

//    @JsonProperty("id")
//    private Long id;
    @JsonProperty("in_time")
    private LocalDateTime inTime;

    @JsonProperty("out_time")
    private LocalDateTime outTime;

    @JsonProperty("status")
    private String status;

    @JsonProperty("working_hours")
    private float workingHours;

    @JsonProperty("overtime_hours")
    private float overtimeHours;

    @JsonProperty("leave_hours")
    private float leaveHours;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("record_date")
    private LocalDate recordDate;

    @JsonProperty("check_in")
    private LocalDateTime checkIn;

    @JsonProperty("check_out")
    private LocalDateTime checkOut;

    @JsonProperty("type_work")
    private String typeWork;

    @JsonProperty("code")
    private String code;

    @JsonProperty("full_name")
    private String fullName;
}
