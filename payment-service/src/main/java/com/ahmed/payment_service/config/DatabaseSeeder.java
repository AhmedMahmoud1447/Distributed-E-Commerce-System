package com.ahmed.payment_service.config;

import com.ahmed.payment_service.domain.UserBalance;
import com.ahmed.payment_service.repositories.UserBalanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * This class is just for developing process.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

    private final UserBalanceRepository userBalanceRepository;

    @Override
    public void run(String... args) {
        if (userBalanceRepository.count() == 0) {
            log.info("Seeding initial user balances into payment database...");

            UserBalance customer1 = UserBalance.builder()
                    .customerId(1L)
                    .balance(BigDecimal.valueOf(1000.00))
                    .build();

            userBalanceRepository.save(customer1);
            log.info("Database seeded successfully. Customer ID 1 has balance: 1000.00");
        }
    }
}