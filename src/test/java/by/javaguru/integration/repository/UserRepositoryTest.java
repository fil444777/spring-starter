package by.javaguru.integration.repository;

import by.javaguru.annotation.IT;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import spring.database.entity.Role;
import spring.database.repository.UserRepository;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.filter;
import static org.junit.jupiter.api.Assertions.*;

@IT
@RequiredArgsConstructor
public class UserRepositoryTest {
    private final UserRepository userRepository;

    @Test
    void checkPageableAllBy() {
        var pageable = PageRequest.of(0, 2, Sort.by("birthDate"));
        var page = userRepository.findAllByRole(Role.ADMIN, pageable);

        page.forEach(u -> System.out.println(u.getFirstname() + " "
                + u.getLastname()
                + " " + u.getBirthDate()));
    }

    @Test
    void checkPageableBirthDate() {
        var pageable = PageRequest.of(0, 2, Sort.by("birthDate"));
        var page = userRepository.findFirst4By(pageable);
        page.forEach(u -> System.out.println(u.getFirstname() + " "
                + u.getLastname()
                + " " + u.getBirthDate()));
    }

    @Test
    void checkPageableFIO() {
        var pageable = PageRequest.of(0, 2, Sort.by("firstname")
                .and(Sort.by("lastname")));
        var page = userRepository.findFirst4By(pageable);
        page.forEach(u -> System.out.println(u.getFirstname() + " "
                + u.getLastname()
                + " " + u.getBirthDate()));
    }

    // Сверху три теста для задания

    @Test
    void findAllAdminsBornBetween1980And1990Test() {
        var admins = userRepository.findAllAdminsBornBetween(
                LocalDate.of(1980, 1, 1),
                LocalDate.of(1990, 12, 31)
        );

        assertNotNull(admins);

        for (var admin : admins) {
            assertEquals(Role.ADMIN, admin.getRole());
        }

        for (var admin : admins) {
            var birthYear = admin.getBirthDate().getYear();
            assertTrue(birthYear >= 1980 && birthYear <= 1990,
                    "Год рождения " + birthYear + " не в диапазоне 1980-1990");
        }
    }

    @Test
    void checkPageable() {
        var pageable = PageRequest.of(1, 2, Sort.by("id"));
        var page = userRepository.findAllBy(pageable);
        page.forEach(u -> System.out.println(u.getId()));
        while (page.hasNext()) {
            page = userRepository.findAllBy(page.nextPageable());
            page.forEach(u -> System.out.println(u.getId()));
            System.out.println(page.getTotalPages());
        }
    }

    @Test
    void findFirst3By() {
        var users = userRepository.findFirst3By(Sort.by("firstname")
                .and(Sort.by("lastname")).descending());
        assertTrue(!users.isEmpty());
        assertThat(users).hasSize(3);
    }

    @Test
    void findFirstByCompanyIsNotNullOrderByIdDescTest() {
        var users = userRepository.findFirst3ByCompanyIsNotNullOrderByIdDesc();
        assertTrue(!users.isEmpty());
        assertThat(users).hasSize(3);
    }

    @Test
    void checkProjections() {
        var users = userRepository.findAllByCompanyId(1);
        assertThat(users).hasSize(2);
    }


    @Test
    void findAllByFirstnameContainingAndLastnameContainingTest() {
        var user = userRepository.findAllByFirstnameContainingAndLastnameContaining("a", "a");
        assertFalse(user.isEmpty());
        assertThat(user).hasSize(3);
    }

    @Test
    void updateRoleTest() {
        var entity1 = userRepository.findById(1L);
        assertEquals(Role.ADMIN, entity1.get().getRole());
        var result = userRepository.updateRole(Role.USER, 1L, 5L);
        assertEquals(2, result);
        var entity2 = userRepository.findById(1L);
        assertEquals(Role.USER, entity2.get().getRole());
    }

}
