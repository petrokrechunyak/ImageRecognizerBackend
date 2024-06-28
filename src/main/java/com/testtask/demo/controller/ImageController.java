package com.testtask.demo.controller;

import com.testtask.demo.service.S3Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/api")
public class ImageController {

    private final S3Service s3Service;

    public ImageController(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @CrossOrigin
    @GetMapping("test")
    public List<Integer> numbers() {
        System.out.println("found request");
        return IntStream.range(0, 50000).limit(15).boxed().toList();
    }

    @CrossOrigin
    @PostMapping("/upload")
    public ResponseEntity uploadFile(@RequestParam("file") MultipartFile file) {
        s3Service.uploadFile(file.getOriginalFilename(), file);
        return ResponseEntity.ok().build();
    }

    @CrossOrigin
    @GetMapping("/search/{label}")
    public ResponseEntity<List<String>> searchImages(@PathVariable("label") String label) {
        List<String> images = s3Service.searchImages(label);
        return ResponseEntity.ok(images);
    }

}
