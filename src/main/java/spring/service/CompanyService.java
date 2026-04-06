package spring.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.database.repository.CompanyRepository;
import spring.dto.CompanyReadDto;
import spring.listener.AccessType;
import spring.listener.EntityEvent;

import java.util.Optional;

@Service
@Transactional
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public CompanyService(CompanyRepository companyRepository, ApplicationEventPublisher applicationEventPublisher) {
        this.companyRepository = companyRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public Optional<CompanyReadDto> findById(Integer id) {
        return companyRepository.findById(id).map(entity -> {
            applicationEventPublisher.publishEvent(new EntityEvent(entity, AccessType.READ));
            return new CompanyReadDto(entity.getId());
        });
    }
}
