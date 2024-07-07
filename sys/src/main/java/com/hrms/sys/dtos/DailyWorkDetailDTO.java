package com.hrms.sys.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyWorkDetailDTO {
    private Long userId;
    private String username;
    private LocalDate workDate;
    private boolean isWorkingDay;
    private boolean isOffDay;
}

