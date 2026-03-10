package com.wsky.lomantin.dto.pexels;

import lombok.Data;

import java.util.List;

@Data
public class PexelsVideoSearchResponse {
    private int page, per_page;
    private List<PexelsVideo> videos;
}
