package com.hrms.sys.responses;

import lombok.*;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VideoResponse {
    private Long id;
    private String title;
    private String idVideo;
//    private String description;
    private int views;
    private String url;
//    private String idVideo;
    private String courseId;
    private String createAt;
    private String imgUrl;
}
