package spring.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import spring.database.entity.UserImage;

import java.util.List;
import java.util.Optional;

public interface UserImageRepository extends JpaRepository<UserImage, Long> {

    List<UserImage> findAllByUserId(Long id);

    @Query("""
    select u
    from UserImage u
    where u.id = :imageId
      and u.user.id = :userId
""")
    Optional<UserImage> findByIdAndUserId(@Param("imageId") Long imageId,
                                          @Param("userId") Long userId);



}
