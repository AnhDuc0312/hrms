package com.hrms.sys.repositories;

import com.hrms.sys.models.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {

    // Truy vấn để lấy danh sách bài viết tin tức kèm thông tin về tác giả
    @Query("SELECT n FROM News n JOIN FETCH n.author")
    List<News> findAllWithAuthors();
}
