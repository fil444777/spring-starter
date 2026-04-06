package spring.service;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Service;
import spring.database.repository.UserRepository;
import spring.mapper.UserMapper;

@Service
@RequiredArgsConstructor
@ToString
public class UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
}
