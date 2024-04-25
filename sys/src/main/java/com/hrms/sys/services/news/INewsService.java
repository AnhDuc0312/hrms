package com.hrms.sys.services.news;

import com.hrms.sys.dtos.NewsDTO;
import com.hrms.sys.models.News;
import com.hrms.sys.models.User;
import com.hrms.sys.responses.NewsResponse;

import java.util.List;

public interface INewsService {
    // Lấy tất cả tin tức kèm thông tin tác giả
    List<NewsResponse> getAllNewsWithAuthors() throws Exception;

    // Lấy tin tức theo ID
    News getNewsById(Long id) throws Exception;

    // Thêm tin tức mới
    News createNews(NewsDTO newsDTO) throws Exception;

    // Cập nhật tin tức
    News updateNews(Long id, News news) throws Exception;

    // Xóa tin tức
    void deleteNews(Long id) throws Exception;
}
