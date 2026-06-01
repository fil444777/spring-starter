package spring.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import spring.dto.UserImageReadDto;

@Slf4j
@Aspect
@Component
public class ImageUploadLoggingAspect {

    private static final long MAX_FILE_SIZE_BYTES = 1 * 1024 * 1024;

    @Pointcut("execution(* spring.service.UserGalleryService.upload(..))")
    public void galleryUploadMethod() {
    }

    @Pointcut("execution(* spring.service.UserGalleryService.replace(..))")
    public void galleryReplaceMethod() {
    }

    @Pointcut("galleryUploadMethod() || galleryReplaceMethod()")
    public void galleryImageMethods() {
    }

    @Before("galleryImageMethods() && args(userId, .., image)")
    public void logImageDetails(JoinPoint joinPoint, Long userId, MultipartFile image) {
        String methodName = joinPoint.getSignature().getName();

        if (image == null || image.isEmpty()) {
            log.info("[{}] Пользователь {} не предоставил файл", methodName, userId);
            return;
        }

        long fileSize = image.getSize();
        String fileName = image.getOriginalFilename();
        String contentType = image.getContentType();

        log.info("===== ЗАГРУЗКА ИЗОБРАЖЕНИЯ =====");
        log.info("Метод: {}", methodName);
        log.info("Пользователь ID: {}", userId);
        log.info("Имя файла: {}", fileName);
        log.info("Размер: {} байт / {} KB / {:.2f} MB",
                fileSize,
                fileSize / 1024,
                fileSize / (1024.0 * 1024.0));
        log.info("Content-Type: {}", contentType);
        log.info("=====================================");
    }

    @Around("galleryImageMethods() && args(userId, .., image)")
    public Object validateImageSize(ProceedingJoinPoint joinPoint, Long userId, MultipartFile image) throws Throwable {

        if (image == null || image.isEmpty()) {
            return joinPoint.proceed();
        }

        long fileSize = image.getSize();
        String fileName = image.getOriginalFilename();

        if (fileSize > MAX_FILE_SIZE_BYTES) {
            String userMessage = String.format(
                    "⚠️ Файл '%s' слишком большой (%.2f MB). " +
                            "Максимальный размер файла: 1 MB. " +
                            "Пожалуйста, выберите изображение меньшего размера или сожмите его.",
                    fileName != null ? fileName : "загруженный файл",
                    fileSize / (1024.0 * 1024.0)
            );

            log.warn(userMessage);

            throw new ResponseStatusException(
                    HttpStatus.PAYLOAD_TOO_LARGE,
                    userMessage
            );
        }

        return joinPoint.proceed();
    }

}
