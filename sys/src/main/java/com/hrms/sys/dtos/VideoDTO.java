package com.hrms.sys.dtos;

import lombok.*;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VideoDTO {
    private String idVideo;
    private String imgUrl;
    private String title;
    private String url;
    private int views;
}
