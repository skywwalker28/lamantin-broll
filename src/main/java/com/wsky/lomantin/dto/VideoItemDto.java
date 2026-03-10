package com.wsky.lomantin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VideoItemDto {
    private long id;
    private String previewImage, authorName, pexelsUrl, videoUrl;
    private int duration;
    private Integer width, height;
}
