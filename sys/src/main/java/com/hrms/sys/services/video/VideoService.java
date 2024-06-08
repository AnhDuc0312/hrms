package com.hrms.sys.services.video;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import com.hrms.sys.models.Course;
import com.hrms.sys.models.Video;
import com.hrms.sys.repositories.CourseRepository;
import com.hrms.sys.repositories.VideoRepository;
import com.hrms.sys.responses.VideoResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.io.IOException;

@Service
@AllArgsConstructor
public class VideoService {

    private final Firestore firestore;
    private final Bucket bucket;
    private final CourseRepository courseRepository;
    private final VideoRepository videoRepository;
    private final String VIDEO_COLLECTION = "videos";
    private final String COURSE_COLLECTION = "courses";

    public Video uploadVideo(String courseId, MultipartFile file, String title) throws Exception {
        Course course = courseRepository.findByIdCourse(courseId);

        String blobName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
        Blob blob = bucket.create(blobName, file.getBytes(), file.getContentType());

        String videoUrl = getVideoUrlWithToken(blob);

        Video video = Video.builder()
                .title(title)
                .url(videoUrl)
                .course(course)
                .views(0)
                .build();

        // Lưu video vào Firestore và lấy ID của nó
        DocumentReference documentReference = firestore.collection(VIDEO_COLLECTION).document();
        video.setIdVideo(documentReference.getId());
        ApiFuture<WriteResult> result = documentReference.set(video);
        result.get(); // Chờ cho tới khi hoàn thành

        // Cập nhật danh sách video trong tài liệu của khóa học
        DocumentReference courseRef = firestore.collection(COURSE_COLLECTION).document(courseId);

        // Thêm video vào danh sách video của khóa học và lưu lại trong cơ sở dữ liệu quan hệ
        List<Video> videoList = course.getVideos() != null ? new ArrayList<>(course.getVideos()) : new ArrayList<>();
        videoList.add(video);
        course.setVideos(videoList);
        courseRef.update("videos", videoList);

        videoRepository.save(video);

        return video;
    }

    public List<VideoResponse> getAllVideosByCourseId(String courseId) {
        Course course = courseRepository.findByIdCourse(courseId);
        List<Video> videos = videoRepository.findAllByCourseId(course.getId());
        List<VideoResponse> videoResponses = new ArrayList<>();
        for (Video video : videos) {
            VideoResponse videoResponse = new VideoResponse();
            videoResponse.setId(video.getId());
            videoResponse.setTitle(video.getTitle());
            videoResponse.setUrl(video.getUrl());
            videoResponse.setViews(video.getViews());
            videoResponse.setCourseId(video.getCourse().getIdCourse());
            videoResponses.add(videoResponse);
        }

        return videoResponses;
    }

    private String getVideoUrlWithToken(Blob blob) throws IOException {
        String downloadTokens = blob.getMetadata().get("downloadTokens");
        String baseUrl = "https://firebasestorage.googleapis.com/v0/b/";
        return baseUrl + bucket.getName() + "/o/" + blob.getName() + "?alt=media&token=" + downloadTokens;
    }
}
