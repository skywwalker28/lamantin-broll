package com.wsky.lomantin.controller;

import com.wsky.lomantin.dto.VideoSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.wsky.lomantin.service.VideoSearchService;

@RestController
@RequiredArgsConstructor
public class VideoSearchController {
    private final VideoSearchService videoSearchService;

    @GetMapping("/api/videos/search")
    public VideoSearchResponse searchVideos(
            @RequestParam String query,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int perPage,
            @RequestParam(required = false) String orientation
    ) {
        return videoSearchService.search(query, page, perPage, orientation);
    }
}
