package com.wsky.lomantin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class VideoSearchResponse {
    private String query;
    private int page, perPage;
    private List<VideoItemDto> items;
}
