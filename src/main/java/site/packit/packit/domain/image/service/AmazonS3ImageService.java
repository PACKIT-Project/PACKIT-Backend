package site.packit.packit.domain.image.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import site.packit.packit.domain.image.exception.ImageException;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static site.packit.packit.domain.image.exception.ImageErrorCode.INVALID_UPLOAD_IMAGE_EXTENSION;
import static site.packit.packit.domain.image.exception.ImageErrorCode.UPLOAD_IMAGE_NOT_FOUNT;

@Service
public class AmazonS3ImageService implements ImageService {

    private static final List<String> ALLOW_IMAGE_EXTENSIONS = List.of("jpg", "png", "jpeg");
    private static final String PROFILE_IMAGE_PATH = "profile-images";

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public AmazonS3ImageService(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    @Override
    public String uploadImage(MultipartFile uploadImage) throws IOException {
        validateUploadImage(uploadImage);

        String storedImageName = PROFILE_IMAGE_PATH + "/"
                + generateStoredImageNameFromOriginalImageName(uploadImage.getOriginalFilename());
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(uploadImage.getSize());
        metadata.setContentType(uploadImage.getContentType());

        amazonS3.putObject(bucket, storedImageName, uploadImage.getInputStream(), metadata);

        return amazonS3.getUrl(bucket, storedImageName).toString();
    }

    private void validateUploadImage(MultipartFile uploadImage) {
        if (uploadImage == null) {
            throw new ImageException(UPLOAD_IMAGE_NOT_FOUNT);
        }

        if (!ALLOW_IMAGE_EXTENSIONS.contains(parseImageExtension(Objects.requireNonNull(uploadImage.getOriginalFilename())))) {
            throw new ImageException(INVALID_UPLOAD_IMAGE_EXTENSION);
        }
    }

    private String parseImageExtension(String originalImageName) {
        return originalImageName.substring(originalImageName.lastIndexOf(".") + 1).toLowerCase();
    }

    private String generateStoredImageNameFromOriginalImageName(String originalImageName) {
        String uuid = UUID.randomUUID().toString();
        String imageExtension = parseImageExtension(originalImageName);

        return uuid + "." + imageExtension;
    }
}
