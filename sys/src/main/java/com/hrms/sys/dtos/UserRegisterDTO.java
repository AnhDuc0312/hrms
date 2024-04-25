package com.hrms.sys.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hrms.sys.models.Role;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegisterDTO {

    private String username;

    private String password;

    @JsonProperty("retype_password")
    private String retypePassword;

    private String email;

    @JsonProperty("role_id")
    private Long roleId;
}
