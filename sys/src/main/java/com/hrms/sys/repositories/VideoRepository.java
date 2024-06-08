package com.hrms.sys.repositories;

import com.hrms.sys.models.Course;
import com.hrms.sys.models.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface VideoRepository extends JpaRepository<Video, Long> {

    List<Video> findAllByCourseId (Long course_id);
}
