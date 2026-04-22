package spring.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

@Service
public class ImageService {

    @Value("${app.image.bucket}")
    private String bucket;

    public void upload(String imagePath, InputStream content) {
        Path fullImagePath = Path.of(bucket, imagePath).normalize();
        try {
            Files.createDirectories(fullImagePath.getParent());
            Files.copy(content, fullImagePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public Optional<byte[]> get(String imagePath) {
        Path fullImagePath = Path.of(bucket, imagePath).normalize();
        try {
            return Files.exists(fullImagePath)
                    ? Optional.of(Files.readAllBytes(fullImagePath))
                    : Optional.empty();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void delete(String path) {
        try {
            Files.deleteIfExists(Path.of(bucket, path).normalize());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
