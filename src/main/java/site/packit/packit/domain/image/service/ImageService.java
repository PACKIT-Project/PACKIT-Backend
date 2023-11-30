package site.packit.packit.domain.image.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageService {

    String uploadImage(MultipartFile uploadImage) throws IOException;
}
