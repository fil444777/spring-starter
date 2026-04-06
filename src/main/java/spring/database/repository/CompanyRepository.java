package spring.database.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import spring.database.entity.Company;
import spring.database.entity.Role;

import java.util.List;
import java.util.Optional;


public interface CompanyRepository extends JpaRepository<Company, Integer> {
    @Query("select c from Company c " +
            "join fetch c.locales cl " +
            "where c.name = :name2")
    Optional<Company> findByName(@Param("name2") String name);

    List<Company> findAllByNameContainingIgnoreCase(String fragment);

    @Modifying(clearAutomatically = true)
    @Query("update Company c set c.name = :name " +
            "where c.id = :id  ")
    int updateCompanyById(Integer id, String name);

    @Query("SELECT c FROM Company c WHERE LOWER(c.name) LIKE LOWER(CONCAT('A', '%'))")
    List<Company> findAllCompaniesStartingWithA();
}
