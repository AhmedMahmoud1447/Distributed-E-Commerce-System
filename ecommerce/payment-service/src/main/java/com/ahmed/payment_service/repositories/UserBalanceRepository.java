package com.ahmed.payment_service.repositories;

import com.ahmed.payment_service.domain.UserBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserBalanceRepository extends JpaRepository<UserBalance, Long> {
    Optional<UserBalance> findByCustomerId(Long customerId);
}