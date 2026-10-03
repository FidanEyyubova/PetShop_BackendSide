package com.petshop.backend.service.file;

import com.petshop.backend.config.AppProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.net.URI;
import java.util.Optional;

/**
 * S3-compatible object storage (AWS S3, Cloudflare R2, MinIO, DigitalOcean Spaces).
 * Enable with STORAGE_TYPE=s3 plus S3_BUCKET, S3_ACCESS_KEY, S3_SECRET_KEY and, for non-AWS, S3_ENDPOINT.
 * STORAGE_PUBLIC_BASE_URL must point at the public bucket / CDN host.
 */
@Service
@ConditionalOnProperty(prefix = "app.storage", name = "type", havingValue = "s3")
public class S3StorageService implements StorageService {

    private final S3Client client;
    private final String bucket;
    private final String publicBaseUrl;

    public S3StorageService(AppProperties properties) {
        AppProperties.Storage.S3 s3 = properties.getStorage().getS3();
        if (s3.getBucket() == null || s3.getBucket().isBlank()) {
            throw new IllegalStateException("S3_BUCKET must be set when STORAGE_TYPE=s3");
        }
        S3ClientBuilder builder = S3Client.builder()
                .region(Region.of(s3.getRegion() == null || s3.getRegion().isBlank() ? "auto" : s3.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(s3.getAccessKey(), s3.getSecretKey())));
        if (s3.getEndpoint() != null && !s3.getEndpoint().isBlank()) {
            builder.endpointOverride(URI.create(s3.getEndpoint()))
                    .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build());
        }
        this.client = builder.build();
        this.bucket = s3.getBucket();
        String base = properties.getStorage().getPublicBaseUrl();
        this.publicBaseUrl = base == null ? "" : base.replaceAll("/+$", "");
    }

    @Override
    public String store(String key, byte[] content, String contentType) {
        client.putObject(PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .contentType(contentType)
                        .cacheControl("public, max-age=31536000, immutable")
                        .build(),
                RequestBody.fromBytes(content));
        return publicBaseUrl + "/" + key;
    }

    @Override
    public Optional<byte[]> load(String key) {
        return Optional.empty();
    }

    @Override
    public void delete(String key) {
        client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(key).build());
    }
}
