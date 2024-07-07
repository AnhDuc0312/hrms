package com.hrms.sys.responses;

import lombok.*;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserChatResponse {
    private Long id;
    private String username;
    private String fullname;
    private String role;
    private String email;
}
