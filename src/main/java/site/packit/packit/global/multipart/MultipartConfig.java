package site.packit.packit.global.multipart;

import jakarta.servlet.MultipartConfigElement;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import static site.packit.packit.domain.image.constant.ImageConstant.MAX_IMAGE_UPLOAD_PER_FILE_SIZE;
import static site.packit.packit.domain.image.constant.ImageConstant.MAX_IMAGE_UPLOAD_SIZE;

@Configuration
public class MultipartConfig {

    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxRequestSize(DataSize.ofBytes(MAX_IMAGE_UPLOAD_SIZE));
        factory.setMaxFileSize(DataSize.ofBytes(MAX_IMAGE_UPLOAD_PER_FILE_SIZE));

        return factory.createMultipartConfig();
    }
}
