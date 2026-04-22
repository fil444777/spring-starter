package spring.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import spring.database.entity.UserImage;
import spring.dto.UserImageReadDto;


@Component
@RequiredArgsConstructor
public class UserImageReadMapper implements Mapper<UserImage, UserImageReadDto> {
    @Override
    public UserImageReadDto map(UserImage object) {
        if (object == null) {
            return null;
        }

        Long userId = object.getUser() != null ? object.getUser().getId() : null;

        String url = userId != null && object.getId() != null
                ? "/api/v1/users/" + userId + "/gallery/" + object.getId() + "/file"
                : null;

        return new UserImageReadDto(
                object.getId(),
                object.getFileName(),
                url
        );
    }
}
