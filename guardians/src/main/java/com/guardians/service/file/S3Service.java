package com.guardians.service.file;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;
import java.nio.file.Paths;

public class S3Service {

    private final S3Client s3Client;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    // 파일 업로드
    public void uploadFile(String bucketName, String keyName, String filePath) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromFile(Paths.get(filePath)));
    }

    // 파일 다운로드
    public void downloadFile(String bucketName, String keyName, String downloadPath) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();

        s3Client.getObject(getObjectRequest, Paths.get(downloadPath));
    }
}
