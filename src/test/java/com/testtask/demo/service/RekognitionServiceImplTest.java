package com.testtask.demo.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.model.DetectLabelsRequest;
import com.amazonaws.services.rekognition.model.DetectLabelsResult;
import com.amazonaws.services.rekognition.model.Label;
import com.testtask.demo.model.ImageLabel;
import com.testtask.demo.repo.ImageLabelRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RekognitionServiceImplTest {

    @Mock
    private AmazonRekognition rekognitionClient;

    @Mock
    private ImageLabelRepo imageLabelRepository;

    @InjectMocks
    private RekognitionServiceImpl rekognitionServiceImpl;

    private static final String BUCKET_NAME = "test-bucket";
    private static final String FILE_NAME = "test-file.jpg";

    @BeforeEach
    public void setUp() {
        rekognitionServiceImpl.setAmazonCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials("access-key", "secret-key")));
        rekognitionServiceImpl.setBucket(BUCKET_NAME);
    }

    @Test
    public void testDetectLabels() {
        // Given
        DetectLabelsResult detectLabelsResult = new DetectLabelsResult()
                .withLabels(new Label().withName("ball"));

        when(rekognitionClient.detectLabels(any(DetectLabelsRequest.class))).thenReturn(detectLabelsResult);

        // When
        rekognitionServiceImpl.detectLabels(FILE_NAME);

        // Then
        verify(rekognitionClient, times(1)).detectLabels(any(DetectLabelsRequest.class));
        verify(imageLabelRepository, times(1)).save(any(ImageLabel.class));
    }
    

    @Test
    public void testProcessLabels() {
        // Given
        List<Label> labels = Arrays.asList(new Label().withName("ball"),
                new Label().withName("soccer"));

        // When
        rekognitionServiceImpl.processLabels(labels, FILE_NAME);

        // Then
        verify(imageLabelRepository, times(2)).save(any(ImageLabel.class));
    }

}
