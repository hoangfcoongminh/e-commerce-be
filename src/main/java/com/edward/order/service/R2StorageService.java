package com.edward.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class R2StorageService {

    private final S3Client r2Client;

    @Value("${cloudflare.r2.bucket-name}")
    private String bucketName;

    @Value("${cloudflare.r2.endpoint}")
    private String endpoint;

    public static final String PRODUCT_IMAGE_FOLDER = "products";

    public String uploadImage(MultipartFile file, String folder) {
        try {
            String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
            String key = folder + "/" + fileName;

            return upload(
                    file.getBytes(),
                    file.getContentType(),
                    key
            );
        } catch (IOException e) {
            throw new RuntimeException("Upload to R2 failed", e);
        }
    }

    public Map<String, String> bulkUpload(List<MultipartFile> files, String folder) {

        Map<String, String> result = new HashMap<>();

        for (MultipartFile file : files) {
            try {
                String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
                String key = folder + "/" + fileName;

                String url = upload(
                        file.getBytes(),
                        file.getContentType(),
                        key
                );

                result.put(file.getOriginalFilename(), url);

            } catch (IOException e) {
                throw new RuntimeException(
                        "Upload failed for file: " + file.getOriginalFilename(), e
                );
            }
        }

        return result;
    }

    private String upload(byte[] data, String contentType, String key) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .build();

        r2Client.putObject(request, RequestBody.fromBytes(data));
        return buildPublicUrl(key);
    }

    private String buildPublicUrl(String key) {
        return endpoint + "/" + bucketName + "/" + key;
    }

    public void deleteImageByKey(String key) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        r2Client.deleteObject(request);
    }

    public void deleteImagesByKeys(List<String> keys) {

        if (keys == null || keys.isEmpty()) {
            return;
        }

        List<ObjectIdentifier> objects = keys.stream()
                .map(key -> ObjectIdentifier.builder().key(key).build())
                .toList();

        DeleteObjectsRequest request = DeleteObjectsRequest.builder()
                .bucket(bucketName)
                .delete(Delete.builder().objects(objects).build())
                .build();

        r2Client.deleteObjects(request);
    }

    private List<String> listKeysByPrefix(String prefix) {

        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(prefix)
                .build();

        ListObjectsV2Response response = r2Client.listObjectsV2(request);

        return response.contents()
                .stream()
                .map(S3Object::key)
                .toList();
    }

    public void deleteImagesByProductId(Long productId) {
        String prefix = PRODUCT_IMAGE_FOLDER + "/" + productId + "/";
        List<String> keys = listKeysByPrefix(prefix);
        deleteImagesByKeys(keys);
    }

    private String extractKeyFromUrl(String url) {
        return url.substring(url.indexOf(bucketName) + bucketName.length() + 1);
    }

}
