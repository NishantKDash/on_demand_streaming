package com.nishant.stream.services;

import com.nishant.stream.models.Video;
import com.nishant.stream.repositories.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VideoService {

    @Autowired
    private VideoRepository videoRepository;

    public void save(Video video){
        videoRepository.save(video);
    }
}
