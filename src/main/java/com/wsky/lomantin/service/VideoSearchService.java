package com.wsky.lomantin.service;


import com.wsky.lomantin.Client.PexelsClient;
import com.wsky.lomantin.dto.VideoItemDto;
import com.wsky.lomantin.dto.VideoSearchResponse;
import com.wsky.lomantin.dto.pexels.PexelsVideo;
import com.wsky.lomantin.dto.pexels.PexelsVideoFile;
import com.wsky.lomantin.dto.pexels.PexelsVideoSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VideoSearchService {
    private final PexelsClient pexelsClient;

    public VideoSearchResponse search(String query, int page, int perPage, String orientation) {
        PexelsVideoSearchResponse response = pexelsClient.searchVideos(query, page, perPage, orientation);

        List<VideoItemDto> items = response.getVideos() == null ? Collections.emptyList() :
                response.getVideos().stream().map(this::toDto).toList();

        return new VideoSearchResponse(query, page, perPage, items);
    }

    private VideoItemDto toDto(PexelsVideo video) {
        String bestVideoUrl = extractBestVideoUrl(video);
        return new VideoItemDto(
                video.getId(),
                video.getImage(),
                video.getUser() != null ? video.getUser().getName() : null,
                video.getUrl(),
                bestVideoUrl,
                video.getDuration(),
                video.getWidth(),
                video.getHeight()
        );
    }

    private String extractBestVideoUrl(PexelsVideo video) {
        if (video.getVideo_files() == null || video.getVideo_files().isEmpty()) return null;
        return video.getVideo_files().stream()
                .filter(file -> "video/mp4".equalsIgnoreCase(file.getFile_type()))
                .max(Comparator.comparingInt(file -> file.getWidth() * file.getHeight()))
                .map(PexelsVideoFile::getLink)
                .orElse(null);
    }
}
