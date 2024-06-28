package com.testtask.demo.service;

import com.testtask.demo.model.ImageLabel;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface S3Service {

    void uploadFile(String key, MultipartFile file);

    List<String> searchImages(String label);
}
