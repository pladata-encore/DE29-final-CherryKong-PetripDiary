package com.example.finalproject.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RequiredArgsConstructor
@Component
public class S3Uploader {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket.profile-image}")
    private String profileImageBucket;

    @Value("${cloud.aws.s3.bucket.place-image}")
    private String recPlaceBucket;


    public String upload(MultipartFile multipartFile, String bucketType) throws IOException {
        File uploadFile = convert(multipartFile)
                .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File로 전환이 실패했습니다."));
        
        String bucketName = getBucketName(bucketType);
        
        return uploadToS3(uploadFile, bucketName);
    }

    private String getBucketName(String bucketType) {
        switch (bucketType) {
            case "profile-image":
                return profileImageBucket;
            case "rec-place":
                return recPlaceBucket;
            default:
                throw new IllegalArgumentException("Invalid bucket type: " + bucketType);
        }
    }

    private String uploadToS3(File uploadFile, String bucketName) {
        String fileName = UUID.randomUUID().toString() + "-" + uploadFile.getName(); // S3에 저장될 파일 이름
        String uploadImageUrl = putS3(uploadFile, fileName, bucketName); // s3에 업로드

        removeNewFile(uploadFile);

        return uploadImageUrl;
    }

    private String putS3(File uploadFile, String fileName, String bucketName) {
        amazonS3Client.putObject(new PutObjectRequest(bucketName, fileName, uploadFile));
        return amazonS3Client.getUrl(bucketName, fileName).toString();
    }

    // MultipartFile을 File로 변환
    private Optional<File> convert(MultipartFile file) throws IOException {
        Path tempDir = Files.createTempDirectory("");
        File convertFile = new File(tempDir.toFile(), file.getOriginalFilename());
        file.transferTo(convertFile);
        return Optional.of(convertFile);
    }

    // 로컬 파일 삭제
    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("파일이 삭제되었습니다.");
        } else {
            log.info("파일이 삭제되지 않았습니다.");
        }
    }

    public void deleteFile(String fileName, String bucketType) throws IOException {
        try {
            String bucketName = getBucketName(bucketType);
            amazonS3Client.deleteObject(bucketName, fileName);
            log.info("S3에서 파일이 삭제되었습니다: " + fileName);
        } catch (SdkClientException e) {
            log.error("S3에서 파일 삭제 중 오류 발생: " + fileName, e);
            throw new IOException("Error Deleting File", e);
        }
    }
    
}
