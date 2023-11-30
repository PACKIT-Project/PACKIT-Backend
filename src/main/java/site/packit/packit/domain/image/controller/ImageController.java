package site.packit.packit.domain.image.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import site.packit.packit.domain.image.dto.response.UploadImageResponse;
import site.packit.packit.domain.image.service.ImageService;
import site.packit.packit.global.response.success.SingleSuccessApiResponse;
import site.packit.packit.global.response.util.ResponseUtil;

import java.io.IOException;

import static org.springframework.http.HttpStatus.OK;

@RequestMapping("/api/images")
@RestController
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping()
    public ResponseEntity<SingleSuccessApiResponse<UploadImageResponse>> uploadImage(MultipartFile uploadImage) throws IOException {
        String savedImageUrl = imageService.uploadImage(uploadImage);
        UploadImageResponse response = UploadImageResponse.of(savedImageUrl);

        return ResponseUtil.successApiResponse(OK, "성공적으로 이미지가 업로드 되었습니다.", response);
    }
}
