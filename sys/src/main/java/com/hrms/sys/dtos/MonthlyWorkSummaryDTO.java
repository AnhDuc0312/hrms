package com.hrms.sys.dtos;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyWorkSummaryDTO {
    private Long userId;
    private String username;
    private String month;
    private int workingDays;
    private int offDays;

}
