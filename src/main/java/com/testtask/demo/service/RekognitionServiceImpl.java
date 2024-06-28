package com.testtask.demo.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.model.*;
import com.testtask.demo.model.ImageLabel;
import com.testtask.demo.repo.ImageLabelRepo;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RekognitionServiceImpl implements RekognitionService {

    private final AmazonRekognition rekognitionClient;
    private final ImageLabelRepo imageLabelRepository;

    @Value("${aws.s3.bucket}")
    @Setter
    private String bucket;

    @Autowired
    @Setter
    private AWSStaticCredentialsProvider amazonCredentials;

    @Autowired
    public RekognitionServiceImpl(AmazonRekognition rekognitionClient, ImageLabelRepo imageLabelRepository) {
        this.rekognitionClient = rekognitionClient;
        this.imageLabelRepository = imageLabelRepository;
    }

    public void detectLabels(String fileName) {
        DetectLabelsRequest request = createDetectLabelsRequest(fileName);

        try {
            DetectLabelsResult result = rekognitionClient.detectLabels(request);
            processLabels(result.getLabels(), fileName);
        } catch (AmazonRekognitionException e) {
            e.printStackTrace();
        }
    }

    DetectLabelsRequest createDetectLabelsRequest(String fileName) {
        return new DetectLabelsRequest()
                .withImage(new Image()
                        .withS3Object(new S3Object()
                                .withName(fileName)
                                .withBucket(bucket)))
                .withMaxLabels(10)
                .withMinConfidence(75F)
                .withRequestCredentialsProvider(amazonCredentials);
    }

    public void processLabels(List<Label> labels, String fileName) {
        for (Label label : labels) {
            ImageLabel imageLabel = new ImageLabel();
            imageLabel.setImageName(fileName);
            imageLabel.setLabel(label.getName().toLowerCase());
            imageLabelRepository.save(imageLabel);
        }
    }
}