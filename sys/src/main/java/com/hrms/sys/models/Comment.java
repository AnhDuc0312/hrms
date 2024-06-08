package com.hrms.sys.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Table(name="comments")
@Entity
@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;
    private String username;
    private String fullName;
    @ManyToOne
    @JoinColumn(name = "video_id")
    private Video video;
}
