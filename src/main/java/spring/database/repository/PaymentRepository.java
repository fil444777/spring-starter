package spring.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import spring.database.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Modifying
    @Query("DELETE FROM Payment p WHERE p.receiver.company.id = :companyId")
    int deleteAllByReceiverCompanyId(@Param("companyId") Integer companyId);
}
