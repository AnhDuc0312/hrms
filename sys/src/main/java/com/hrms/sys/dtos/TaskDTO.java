package com.hrms.sys.dtos;

import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {
    private String username;
    private String name;
    private String description;
    private String status;
}
