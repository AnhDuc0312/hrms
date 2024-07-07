package com.hrms.sys.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tasks")
@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taskId;

    private Long userId;

    private String username;

    private String fullname;

    private String name;

    private String description;

    private String status;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    private Long managerId;
}
