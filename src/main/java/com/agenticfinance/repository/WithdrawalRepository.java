package com.agenticfinance.repository;

import com.agenticfinance.domain.Withdrawal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WithdrawalRepository extends JpaRepository<Withdrawal, String> {

    List<Withdrawal> findByPortfolioIdOrderByRequestedAtDesc(String portfolioId, org.springframework.data.domain.Pageable pageable);
}
