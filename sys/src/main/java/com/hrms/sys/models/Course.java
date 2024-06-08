package com.hrms.sys.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Table(name="courses")
@Entity
@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String idCourse;

    private String title;
    private String description;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Video> videos;

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", idCourse='" + idCourse + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }


}
