package com.nishant.stream.controllers;

import com.nishant.stream.services.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class uploadController {



    @Autowired
    private UrlService urlService;
    @Value("${aws.bucket}")
    private String bucketName;

    @GetMapping("/video")
    public ResponseEntity<String> uploadOriginalVideo(@RequestParam String key)
    {
         String preSignedUrl = urlService.createPresignedUrl(bucketName, key);
         return ResponseEntity.ok(preSignedUrl);
    }
}
