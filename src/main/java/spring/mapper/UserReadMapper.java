package spring.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import spring.database.entity.User;
import spring.dto.CompanyReadDto;
import spring.dto.UserReadDto;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserReadMapper implements Mapper<User, UserReadDto> {

    private final CompanyReadMapper companyReadMapper;

    @Override
    public UserReadDto map(User object) {
        CompanyReadDto company = Optional.ofNullable(object.getCompany())
                .map(companyReadMapper::map)
                .orElse(null);
        return new UserReadDto(
                object.getId(),
                object.getUsername(),
                object.getPassword(),
                object.getBirthDate(),
                object.getFirstname(),
                object.getLastname(),
                object.getRole(),
                company
        );
    }
}
