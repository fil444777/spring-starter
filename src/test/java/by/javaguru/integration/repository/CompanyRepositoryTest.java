package by.javaguru.integration.repository;

import by.javaguru.annotation.IT;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import spring.database.entity.Company;
import spring.database.entity.Role;
import spring.database.repository.CompanyRepository;
import spring.database.repository.UserRepository;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@IT
@RequiredArgsConstructor
public class CompanyRepositoryTest {
    private static final Integer APPLE_ID = 4;
    private final EntityManager entityManager;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    @Test
    void deleteAllCompaniesStartingWithATest() {

        var companiesToDelete = companyRepository.findAllCompaniesStartingWithA();

        for (var company : companiesToDelete) {
            companyRepository.delete(company);
        }

        var remaining = companyRepository.findAllCompaniesStartingWithA();
        assertTrue(remaining.isEmpty());
    }

    @Test
    void updateCompanyByIdTest() {
        var entity1 = companyRepository.findById(1);
        assertEquals("Google", entity1.get().getName());
        var result = companyRepository.updateCompanyById(1, "Yandex");
        assertEquals(1, result);
        var entity2 = companyRepository.findById(1);
        assertEquals("Yandex", entity2.get().getName());
    }

    @Test
    void checkFindByQueries() {
        companyRepository.findByName("Google");
        var companies = companyRepository.findAllByNameContainingIgnoreCase("a");
        assertThat(companies).hasSize(3);
    }

    @Test
    void delete() {
        var maybeCompany = companyRepository.findById(APPLE_ID);
        assertTrue(maybeCompany.isPresent());
        maybeCompany.ifPresent(companyRepository::delete);
        entityManager.flush();
        entityManager.clear();
        assertTrue(companyRepository.findById(APPLE_ID).isEmpty());
    }


    @Test
    void findById() {
        var company = entityManager.find(Company.class, 1);
        assertNotNull(company);
        assertThat(company.getLocales()).hasSize(2);
    }

    @Test
    void save() {
        var company = Company.builder()
                .name("Apple12")
                .locales(Map.of(
                        "ru", "Apple описание",
                        "en", "Apple description"
                ))
                .build();
        entityManager.persist(company);
        assertNotNull(company.getId());
    }
}
