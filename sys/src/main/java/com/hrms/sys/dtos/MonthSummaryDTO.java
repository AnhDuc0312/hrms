package com.hrms.sys.dtos;


import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MonthSummaryDTO {
    private String month;
    private int working;
    private int off;
}
