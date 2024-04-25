package com.hrms.sys.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hrms.sys.models.Role;
import com.hrms.sys.models.User;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;

    private String username;

    @JsonProperty("employee_id")
    private Long employeeId;

    @JsonProperty("department_id")
    private Long departmentId;

    @JsonProperty("role")
    private Role role;
    public static UserResponse fromUser(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }

}
