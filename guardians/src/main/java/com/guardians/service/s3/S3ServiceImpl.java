package com.guardians.service.s3;

import com.guardians.config.AwsS3Properties;
import com.guardians.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

    private final S3Client s3Client;
    private final AwsS3Properties awsS3Properties;
    private final UserRepository userRepository;

    @Override
    public String uploadProfileImage(MultipartFile file) throws IOException {
        String key = "profile/" + UUID.randomUUID() + "-" + file.getOriginalFilename();

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(awsS3Properties.getBucket())
                        .key(key)
                        .acl("public-read")
                        .contentType(file.getContentType())
                        .build(),
                RequestBody.fromBytes(file.getBytes())
        );

        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                awsS3Properties.getBucket(),
                awsS3Properties.getRegion(),
                key
        );
    }

    @Override
    public void deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.equals(getDefaultProfileUrl())) return;

        String key = imageUrl.substring(imageUrl.indexOf(".com/") + 5);
        s3Client.deleteObject(builder -> builder
                .bucket(awsS3Properties.getBucket())
                .key(key)
        );
    }

    @Override
    public String getDefaultProfileUrl() {
        return awsS3Properties.getDefaultProfileUrl();
    }


}
