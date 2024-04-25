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
public class RemoteDTO {

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("from_datetime")
    private LocalDateTime fromDatetime;

    @JsonProperty("to_datetime")
    private LocalDateTime toDatetime;

    @JsonProperty("total_hours")
    private Float totalHours;

    @JsonProperty("reason")
    private String reason;

    @JsonProperty("evident")
    private String evident;

    @JsonProperty("approver_id")
    private Long approverId;

    @JsonProperty("comment")
    private String comment;

    @JsonProperty("status")
    private String status;
}
