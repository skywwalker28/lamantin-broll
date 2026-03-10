package com.wsky.lomantin.dto.pexels;

import lombok.Data;

import java.util.List;

@Data
public class PexelsVideo {
    private long id;
    private int width, height, duration;
    private String url, image;
    private PexelsUser user;
    private List<PexelsVideoFile> video_files;
}
