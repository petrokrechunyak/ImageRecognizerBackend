package com.testtask.demo.service;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.testtask.demo.model.ImageLabel;
import com.testtask.demo.repo.ImageLabelRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class S3ServiceTest {

    @Mock
    private AmazonS3 amazonS3;

    @Mock
    private RekognitionService rekognitionService;

    @Mock
    private ImageLabelRepo imageLabelRepository;

    @InjectMocks
    private S3ServiceImpl s3Service;

    private static final String BUCKET_NAME = "test-bucket";
    private static final String FILE_NAME = "test-file.jpg";
    private static final String LABEL_NAME = "ball";

    @BeforeEach
    public void setUp() {
        s3Service.setBucket(BUCKET_NAME);
    }

    @Test
    public void testUploadFile() throws IOException {

        // Given
        String key = "test-key";
        byte[] imageBytes = "image-content".getBytes();
        MultipartFile multipartFile = new MockMultipartFile(
                "file",
                FILE_NAME,
                "image/jpeg",
                imageBytes
        );

        when(amazonS3.putObject(any(PutObjectRequest.class))).thenReturn(null);

        // When
        s3Service.uploadFile(key, multipartFile);

        // Then
        verify(amazonS3, times(1)).putObject(any(PutObjectRequest.class));
        verify(rekognitionService, times(1)).detectLabels(any(String.class));
    }

    @Test
    public void testSearchImages() {
        // Given
        List<ImageLabel> imageLabels = Arrays.asList(
                new ImageLabel(FILE_NAME, LABEL_NAME)
        );

        when(imageLabelRepository.findAllByLabel(any())).thenReturn(imageLabels);

        // When
        List<String> result = s3Service.searchImages(LABEL_NAME);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(FILE_NAME, result.get(0));
    }
}
