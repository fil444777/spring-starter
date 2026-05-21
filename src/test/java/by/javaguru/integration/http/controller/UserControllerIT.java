package by.javaguru.integration.http.controller;

import by.javaguru.annotation.IT;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import spring.database.entity.Role;
import spring.database.entity.User;
import spring.database.repository.UserRepository;
import spring.dto.UserReadDto;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static spring.dto.UserCreateEditDto.Fields.*;

@IT
@AutoConfigureMockMvc
@RequiredArgsConstructor
public class UserControllerIT {
    private final MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;

    @Test
    void findAll() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("user/users"))
                .andExpect(model().attributeExists("users"));
    }

    @Test
    void create() throws Exception {
        mockMvc.perform(post("/users")
                        .with(csrf())
                        .param(username, "test@gmail.com")
                        .param(firstname, "Test")
                        .param(password, "Test")
                        .param(lastname, "TestTest")
                        .param(role, "ADMIN")
                        .param(companyId, "1")
                        .param(birthDate, "2000-01-01")

                )
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrlPattern("/users/{\\d+}")
                );
    }

    @Test
    void delete() throws Exception {
        User testUser = new User();
        testUser.setUsername("test@gmail.com");
        testUser.setPassword("securePass123");
        testUser.setFirstname("Delete");
        testUser.setLastname("Me");
        testUser.setBirthDate(LocalDate.of(2000, 1, 1));
        testUser.setRole(Role.ADMIN);

        User savedUser = userRepository.save(testUser);
        Long userId = savedUser.getId();

        mockMvc.perform(post("/users/{id}/delete", userId)
                        .with(csrf()))
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/users")
                );

        Optional<User> maybeUser = userRepository.findById(userId);
        assertThat(maybeUser).isEmpty();
    }

    @Test
    void update() throws Exception {
        User initialUser = new User();
        initialUser.setUsername("test@gmail.com");
        initialUser.setPassword("secureOldPass123");
        initialUser.setFirstname("Old");
        initialUser.setLastname("User");
        initialUser.setBirthDate(LocalDate.of(1990, 1, 1));
        initialUser.setRole(Role.USER);

        User savedUser = userRepository.save(initialUser);
        Long userId = savedUser.getId();

        mockMvc.perform(post("/users/{id}/update", userId)
                        .with(csrf())
                        .param("username", "test@gmail.com")
                        .param("firstname", "Updated")
                        .param("lastname", "Name")
                        .param("birthDate", "2000-05-20")
                        .param("role", "ADMIN")
                        .param("password", ""))
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/users/" + userId)
                );

        User updatedUser = userRepository.findById(userId).orElseThrow();

        assertThat(updatedUser.getUsername()).isEqualTo("test@gmail.com");
        assertThat(updatedUser.getFirstname()).isEqualTo("Updated");
        assertThat(updatedUser.getBirthDate()).isEqualTo(LocalDate.of(2000, 5, 20));
        assertThat(updatedUser.getRole()).isEqualTo(Role.ADMIN);

        assertThat(updatedUser.getPassword()).isEqualTo("secureOldPass123");
    }

}
