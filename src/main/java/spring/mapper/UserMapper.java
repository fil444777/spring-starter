package spring.mapper;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import spring.dto.UserDto;

@Component
@ToString
public class UserMapper {
    @Autowired
    private UserDto userDto;
}
