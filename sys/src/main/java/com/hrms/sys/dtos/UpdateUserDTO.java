package com.hrms.sys.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateUserDTO {
    @JsonProperty("username")
    private String username;



    @JsonProperty("password")
    private String password;

    @JsonProperty("retype_password")
    private String retypePassword;


}
