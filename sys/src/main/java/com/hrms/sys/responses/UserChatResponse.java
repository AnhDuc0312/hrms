package com.hrms.sys.responses;

import lombok.*;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserChatResponse {
    private String username;
    private String fullname;
}
