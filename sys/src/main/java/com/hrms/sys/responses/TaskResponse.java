package com.hrms.sys.responses;

import com.hrms.sys.models.Task;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskResponse {
    private Long taskId;
    private Long userId;
    private String username;
    private String fullName;
    private String name;
    private String description;
    private String status;
    private Long departmentId;
    private String departmentName;  // Optional, if you want to include department name
    private Long managerId;

    public static TaskResponse convertToTaskResponse(Task task) {
        if (task == null) {
            return null;
        }

        return TaskResponse.builder()
                .taskId(task.getTaskId())
                .userId(task.getUserId())
                .username(task.getUsername())
                .fullName(task.getFullname())
                .name(task.getName())
                .description(task.getDescription())
                .status(task.getStatus())
                .departmentId(task.getDepartment() != null ? task.getDepartment().getId() : null)
                .departmentName(task.getDepartment() != null ? task.getDepartment().getName() : null)
                .managerId(task.getManagerId())
                .build();
    }
}
