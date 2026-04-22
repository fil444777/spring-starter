package spring.http.rest;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import spring.database.entity.UserImage;
import spring.dto.UserImageReadDto;
import spring.service.UserGalleryService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users/{userId}/gallery")
public class UserGalleryRestController {

    private final UserGalleryService userGalleryService;

    @GetMapping
    public List<UserImageReadDto> findAll(@PathVariable Long userId) {
        return userGalleryService.findAll(userId);
    }

    @GetMapping("/{imageId}")
    public UserImageReadDto findById(@PathVariable Long userId,
                                     @PathVariable Long imageId) {
        return userGalleryService.findById(userId, imageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public UserImageReadDto upload(@PathVariable Long userId,
                                   @RequestParam("image") MultipartFile image) {
        return userGalleryService.upload(userId, image);
    }

    @PutMapping(value = "/{imageId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UserImageReadDto replace(@PathVariable Long userId,
                                    @PathVariable Long imageId,
                                    @RequestParam("image") MultipartFile image) {
        return userGalleryService.replace(userId, imageId, image);
    }

    @DeleteMapping("/{imageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long userId,
                       @PathVariable Long imageId) {
        userGalleryService.delete(userId, imageId);
    }

    @GetMapping("/{imageId}/file")
    public ResponseEntity<byte[]> file(@PathVariable Long userId,
                                       @PathVariable Long imageId) {
        UserImage image = userGalleryService.findEntity(userId, imageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return userGalleryService.findFile(userId, imageId)
                .map(content -> ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(
                                image.getContentType() == null ? MediaType.APPLICATION_OCTET_STREAM_VALUE : image.getContentType()))
                        .body(content))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
