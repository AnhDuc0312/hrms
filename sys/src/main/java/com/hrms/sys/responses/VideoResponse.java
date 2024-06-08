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
//    private String description;
    private int views;
    private String url;
//    private String idVideo;
    private String courseId;
}
