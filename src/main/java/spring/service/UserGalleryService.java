package spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import spring.database.entity.User;
import spring.database.entity.UserImage;
import spring.database.repository.UserImageRepository;
import spring.database.repository.UserRepository;
import spring.dto.UserImageReadDto;
import spring.mapper.UserImageReadMapper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserGalleryService {

    private final UserRepository userRepository;
    private final UserImageRepository userImageRepository;
    private final UserImageReadMapper userImageReadMapper;
    private final ImageService imageService;

    public List<UserImageReadDto> findAll(Long userId) {
        return userImageRepository.findAllByUserId(userId)
                .stream()
                .map(userImageReadMapper::map)
                .toList();
    }

    public Optional<UserImageReadDto> findById(Long userId, Long imageId) {
        return userImageRepository.findByIdAndUserId(imageId, userId)
                .map(userImageReadMapper::map);
    }

    @Transactional
    public UserImageReadDto upload(Long userId, MultipartFile image) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        String originalName = image.getOriginalFilename();
        if (originalName == null || originalName.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File name is empty");
        }

        String storedName = UUID.randomUUID() + "_" + originalName;
        saveFile(storedName, image);

        UserImage entity = new UserImage();
        entity.setUser(user);
        entity.setFileName(originalName);
        entity.setPath(storedName);
        entity.setContentType(image.getContentType());

        return userImageReadMapper.map(userImageRepository.save(entity));
    }

    @Transactional
    public UserImageReadDto replace(Long userId, Long imageId, MultipartFile image) {
        UserImage existing = userImageRepository.findByIdAndUserId(imageId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        String originalName = image.getOriginalFilename();
        if (originalName == null || originalName.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File name is empty");
        }

        String storedName = UUID.randomUUID() + "_" + originalName;
        imageService.delete(existing.getPath());
        saveFile(storedName, image);

        existing.setFileName(originalName);
        existing.setPath(storedName);
        existing.setContentType(image.getContentType());

        return userImageReadMapper.map(userImageRepository.save(existing));
    }

    @Transactional
    public void delete(Long userId, Long imageId) {
        UserImage existing = userImageRepository.findByIdAndUserId(imageId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        imageService.delete(existing.getPath());
        userImageRepository.delete(existing);
    }

    public Optional<byte[]> findFile(Long userId, Long imageId) {
        return userImageRepository.findByIdAndUserId(imageId, userId)
                .map(UserImage::getPath)
                .flatMap(imageService::get);
    }

    public Optional<UserImage> findEntity(Long userId, Long imageId) {
        return userImageRepository.findByIdAndUserId(imageId, userId);
    }

    private void saveFile(String fileName, MultipartFile image) {
        try {
            imageService.upload(fileName, image.getInputStream());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
