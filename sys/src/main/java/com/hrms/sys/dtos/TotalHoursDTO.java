package com.hrms.sys.dtos;

import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TotalHoursDTO {

    private Long userId;

    private float totalWorkingHours;

    private float totalOvertimeHours;

    private float totalLeaveHours;
}
