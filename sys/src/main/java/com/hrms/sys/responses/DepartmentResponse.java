package com.hrms.sys.responses;

import com.hrms.sys.models.Department;
import com.hrms.sys.models.Employee;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class DepartmentResponse {
    private Long id;
    private String name;
    private String managerId;
    private String managerName;
    private String managerUsername;


}
