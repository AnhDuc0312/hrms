package com.hrms.sys.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeaveDTO {

    @JsonProperty("from_datetime")
    private LocalDateTime fromDatetime;

    @JsonProperty("to_datetime")
    private LocalDateTime toDatetime;

    @JsonProperty("type")
    private String type;

    @JsonProperty("reason")
    private String reason;

    @JsonProperty("total_hours")
    private Float totalHours;

    @JsonProperty("evident")
    private String evident;

    @JsonProperty("approver_id")
    private Long approverId;

    @JsonProperty("comment")
    private String comment;

    @JsonProperty("status")
    private String status;
}
