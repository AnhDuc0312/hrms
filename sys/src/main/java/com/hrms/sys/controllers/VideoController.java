package com.hrms.sys.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrms.sys.dtos.VideoDTO;
import com.hrms.sys.exceptions.InvalidDataException;
import com.hrms.sys.models.Video;
import com.hrms.sys.responses.VideoResponse;
import com.hrms.sys.services.video.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    @PostMapping("/upload")
    public ResponseEntity<VideoDTO> uploadVideo(
            @RequestParam("courseId") String courseId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title) {
        try {
            VideoDTO video = videoService.uploadVideo(courseId, file, title);
            return ResponseEntity.status(HttpStatus.CREATED).body(video);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<VideoResponse>> getAllVideosByCourseId(@PathVariable String courseId) {
        try {
            List<VideoResponse> videos = videoService.getAllVideosByCourseId(courseId);
            return ResponseEntity.ok(videos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{videoId}")
    public ResponseEntity<?> deleteVideo(@PathVariable String videoId) {
        try{
            videoService.deleteVideo(videoId);
            return ResponseEntity.ok("{\"message\": \"Deleted video successfully\"}");

        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{videoId}")
    public ResponseEntity<?> updateVideo(@PathVariable String videoId, @RequestBody VideoDTO video) {
        try {
            videoService.updateVideo(videoId,video);
            return ResponseEntity.ok().body("{\"message\": \"Password updated successfully\"}");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


        private final RestTemplate restTemplate;

    private final String baseUrl = "https://firebasestorage.googleapis.com/v0/b/";

    @GetMapping("/getDownloadUrl")
    public String getDownloadUrl() {
        String bucketName = "hrms-ee8d5.appspot.com"; // replace with your bucket name
        String blobName = "36ab61b6-0ca2-422d-b4fb-5ce295ecfa14-004%20Q%20&%20A.mp4";
        String urlString = baseUrl + bucketName + "/o/" + blobName + "?alt=media";

        try {
            // Fetch JSON data
            ResponseEntity<String> response = restTemplate.getForEntity(urlString, String.class);
            if (response.getStatusCodeValue() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatusCodeValue());
            }

            // Parse JSON data
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());

            // Extract downloadTokens
            String downloadTokens = root.path("downloadTokens").asText();

            // Create new URL with download token
            String newUrlString = urlString + "&token=" + downloadTokens;
            return newUrlString;

        } catch (Exception e) {
            e.printStackTrace();
            return "Error occurred: " + e.getMessage();
        }
    }
}
