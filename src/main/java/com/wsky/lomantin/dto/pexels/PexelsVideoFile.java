package com.wsky.lomantin.dto.pexels;

import lombok.Data;

@Data
public class PexelsVideoFile {
    private long id;
    private String quality, file_type, link;
    private int width, height;
    private double fps;
}
