package com.hrms.sys.controllers;

import com.hrms.sys.dtos.NewsDTO;
import com.hrms.sys.models.News;
import com.hrms.sys.responses.NewsResponse;
import com.hrms.sys.services.news.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;



    @GetMapping("")
    public ResponseEntity<List<NewsResponse>> getAllNews() {
        try {
            List<NewsResponse> newsList = newsService.getAllNewsWithAuthors();
            return new ResponseEntity<>(newsList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<News> createNews(@RequestBody NewsDTO newsDTO) {
        try {
            News createdNews = newsService.createNews(newsDTO);
            return new ResponseEntity<>(createdNews , HttpStatus.CREATED);
        }catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getNewsById (@PathVariable("id") Long id) {
        try{
            News news = newsService.getNewsById(id);
            return new ResponseEntity<>(news,HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<News> updateNews(@PathVariable("id") Long id, @RequestBody NewsDTO newsDTO) {
        try {
            News updatedNews = newsService.updateNews(id, newsDTO);
            return new ResponseEntity<>(updatedNews, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNews(@PathVariable("id") Long id) {
        try {
            newsService.deleteNews(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
