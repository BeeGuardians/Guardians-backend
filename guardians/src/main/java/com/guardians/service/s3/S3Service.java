package com.guardians.service.s3;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface S3Service {
    String uploadProfileImage(MultipartFile file) throws IOException;
    void deleteImage(String imageUrl);
    String getDefaultProfileUrl();
}
