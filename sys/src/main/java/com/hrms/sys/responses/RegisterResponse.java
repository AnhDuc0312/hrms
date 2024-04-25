package com.hrms.sys.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hrms.sys.models.User;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {
    @JsonProperty("message")
    private String message;

    @JsonProperty("user")
    private User user;
}
