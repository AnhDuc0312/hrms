package com.hrms.sys.repositories;

import com.hrms.sys.models.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
    Course findByIdCourse(String name);
    Course deleteByIdCourse(String name);
}
