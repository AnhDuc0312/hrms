package com.hrms.sys.services.news;

import com.hrms.sys.dtos.NewsDTO;
import com.hrms.sys.models.News;
import com.hrms.sys.models.User;
import com.hrms.sys.repositories.NewsRepository;
import com.hrms.sys.repositories.UserRepository;
import com.hrms.sys.responses.NewsResponse;
import com.hrms.sys.services.news.INewsService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class NewsService implements INewsService {

    private final NewsRepository newsRepository;

    private final UserRepository userRepository;


    // Lấy tất cả các bài viết tin tức với thông tin về tác giả
    public List<NewsResponse> getAllNewsWithAuthors() throws Exception{
        return newsRepository.findAll().stream()
                .map(news -> NewsResponse.builder()
                        .id(news.getId())
                        .title(news.getTitle())
                        .content(news.getContent())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public News getNewsById(Long id) throws Exception {
        return newsRepository.findById(id).orElse(null);
    }

    // Tạo mới một bài viết tin tức
    public News createNews(NewsDTO newsDTO) throws Exception{
        // Lấy thông tin về người dùng đang đăng nhập
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new Exception("Người dùng chưa đăng nhập.");
        }

        // Lấy username của người dùng đăng nhập
        String loggedInUsername = authentication.getName();

        // Truy vấn cơ sở dữ liệu để lấy thông tin chi tiết về người dùng từ username
        Optional<User> author = userRepository.findByUsername(loggedInUsername);
        if (author.isEmpty()) {
            throw new Exception("Không tìm thấy thông tin người dùng.");
        }

        // Gán thông tin người dùng cho đối tượng NewsDTO
        News news = new News();
        news.setAuthor(author.get());
        news.setTitle(newsDTO.getTitle());
        news.setContent(newsDTO.getContent());
        news.setTime(LocalDateTime.now());

        // Tiếp tục với quá trình tạo bài viết
        newsRepository.save(news);
        return null;

    }

    // Cập nhật một bài viết tin tức
    public News updateNews(Long id, News updatedNews) throws Exception{
        News existingNews = newsRepository.findById(id).orElse(null);
        if (existingNews != null) {
            existingNews.setTitle(updatedNews.getTitle());
            existingNews.setContent(updatedNews.getContent());
            // Cập nhật thông tin khác nếu cần
            return newsRepository.save(existingNews);
        }
        return null;
    }

    // Xóa một bài viết tin tức
    public void deleteNews(Long id) throws Exception{
        newsRepository.deleteById(id);
    }
}
