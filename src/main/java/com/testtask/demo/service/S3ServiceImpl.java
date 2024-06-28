package com.testtask.demo.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.testtask.demo.model.ImageLabel;
import com.testtask.demo.repo.ImageLabelRepo;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class S3ServiceImpl implements S3Service {

    @Setter
    @Value("${aws.s3.bucket}")
    private String bucket;

    private final RekognitionService rekognitionService;
    private final AmazonS3 amazonS3;
    private final ImageLabelRepo imageLabelRepository;

    @Autowired
    public S3ServiceImpl(RekognitionService rekognitionService, AmazonS3 amazonS3, ImageLabelRepo imageLabelRepository) {
        this.rekognitionService = rekognitionService;
        this.amazonS3 = amazonS3;
        this.imageLabelRepository = imageLabelRepository;
    }

    @Override
    public void uploadFile(String key, MultipartFile file) {
        File uploadFile = multipartToFile(file);
        amazonS3.putObject(new PutObjectRequest(bucket, key, uploadFile));

        uploadFile.delete();
        rekognitionService.detectLabels(uploadFile.getName());
    }

    @Override
    public List<String> searchImages(String label) {
        List<ImageLabel> imageLabels = imageLabelRepository.findAllByLabel(label.toLowerCase());
        return imageLabels.stream().map(ImageLabel::getImageName).collect(Collectors.toList());
    }

    public File multipartToFile(MultipartFile file) {
        File uploadFile = new File(file.getOriginalFilename());
        try {
            uploadFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(uploadFile);
            fos.write(file.getBytes());
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return uploadFile;
    }

}
