package com.jigumulmi.aws;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.DeleteObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
public class S3Service {

    @Value("${cloud.aws.s3.bucket}")
    public String bucket;
    private final Duration DEFAULT_DURATION = Duration.ofMinutes(60);

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    public void putObject(String bucketName, String key, MultipartFile file)
        throws IOException, SdkException {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .contentType(file.getContentType())
            .contentLength(file.getSize())
            .build();

        s3Client.putObject(putObjectRequest,
            RequestBody.fromInputStream(file.getInputStream(), file.getSize())
        );
    }

    public void deleteObject(String bucketName, String key) throws SdkException {
        if (key == null) {
            return;
        }

        DeleteObjectRequest request = DeleteObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();

        s3Client.deleteObject(request);
    }

    public void deleteObjects(String bucketName, List<ObjectIdentifier> objectIdentifierList)
        throws SdkException {
        if (objectIdentifierList.isEmpty()) {
            return;
        }

        DeleteObjectsRequest deleteObjectsRequest = DeleteObjectsRequest.builder()
            .bucket(bucketName)
            .delete(Delete.builder().objects(objectIdentifierList).build())
            .build();

        s3Client.deleteObjects(deleteObjectsRequest);
    }

    public String generatePutObjectPresignedUrl(String bucketName, String key) {
        PutObjectPresignRequest request = PutObjectPresignRequest.builder()
            .putObjectRequest(
                PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build()
            )
            .signatureDuration(DEFAULT_DURATION)
            .build();

        return s3Presigner.presignPutObject(request).url().toString();
    }

    public String generateDeleteObjectPresignedUrl(String bucketName, String key) {
        DeleteObjectPresignRequest request = DeleteObjectPresignRequest.builder()
            .deleteObjectRequest(
                DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build()
            )
            .signatureDuration(DEFAULT_DURATION)
            .build();

        return s3Presigner.presignDeleteObject(request).url().toString();
    }
}
