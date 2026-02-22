package com.agenticfinance.repository;

import com.agenticfinance.domain.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PortfolioRepository extends JpaRepository<Portfolio, String> {

    @Query("SELECT p FROM Portfolio p LEFT JOIN FETCH p.positions WHERE p.id = :id")
    Optional<Portfolio> findByIdWithPositions(String id);
}
