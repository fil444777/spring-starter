package by.javaguru.unit;

import by.javaguru.annotation.IT;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.TestConstructor;
import spring.database.repository.CompanyRepository;
import spring.database.entity.Company;
import spring.dto.CompanyReadDto;
import spring.listener.EntityEvent;
import spring.service.CompanyService;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@IT
@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class CompanyServiceTest {
    private static final Integer COMPANY_ID = 1;

    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @InjectMocks
    private CompanyService companyService;

//    @Test
//    void findById() {
//        Mockito.doReturn(Optional.of(new Company(COMPANY_ID, null, Collections.emptyMap())))
//                .when(companyRepository).findById(COMPANY_ID);
//
//        var actualResult = companyService.findById(COMPANY_ID);
//
//        assertTrue(actualResult.isPresent());
//
//        var expectedResult = new CompanyReadDto(COMPANY_ID);
//
//        actualResult.ifPresent(actual -> assertEquals(expectedResult, actual));
//
//        verify(eventPublisher).publishEvent(any(EntityEvent.class));
//    }



}
