package com.hrms.sys.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Data@Builder
@NoArgsConstructor
public class EmployeeListResponse {
    private List<EmployeeResponse> employees;
    private int totalPages;
}
