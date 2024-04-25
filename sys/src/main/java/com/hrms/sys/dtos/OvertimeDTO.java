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
public class OvertimeDTO {

    @JsonProperty("from_datetime")
    private LocalDateTime fromDatetime;

    @JsonProperty("to_datetime")
    private LocalDateTime toDatetime;

    @JsonProperty("reason")
    private String reason;

    @JsonProperty("evident")
    private String evident;

    @JsonProperty("comment")
    private String comment;

    @JsonProperty("total_hours")
    private Float totalHours;
}
