package spring.database.repository;


import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spring.database.entity.Role;
import spring.database.entity.User;
import spring.dto.IPersonalInfo;
import spring.dto.PersonalInfo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Page<User> findAllBy(Pageable pageable);

    List<User> findFirst3By(Sort sort);

    List<User> findFirst3ByCompanyIsNotNullOrderByIdDesc();

    @Query(value = "select u.firstname, u.lastname, u.birth_date from users u" +
            "where company_id = :companyId",
            nativeQuery = true)
    List<IPersonalInfo> findAllByCompanyId(Integer companyId);

    @Query("select u from User  u " +
            "where u.firstname like %:firstname% and u.lastname like %:lastname%")
    List<User> findAllByFirstnameContainingAndLastnameContaining(String firstname, String lastname);

    @Query(value = "SELECT u.* FROM users u WHERE u.username = :username",
            nativeQuery = true)
    List<User> findAllByUsername(String username);

    @Modifying(clearAutomatically = true)
    @Query("update User u set u.role = :role " +
            "where u.id in (:ids)")
    int updateRole(Role role, Long... ids);

    @Query("SELECT u FROM User u WHERE u.role = 'ADMIN' " +
            "AND u.birthDate BETWEEN :startDate AND :endDate")
    List<User> findAllAdminsBornBetween(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Modifying
    @Query("DELETE FROM User u WHERE u.company.id = :companyId")
    int deleteAllByCompanyId(@Param("companyId") Integer companyId);
}
