package com.agenticfinance.repository;

import com.agenticfinance.domain.RiskSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RiskSnapshotRepository extends JpaRepository<RiskSnapshot, String> {

    List<RiskSnapshot> findByPortfolioIdOrderByTimestampDesc(String portfolioId, org.springframework.data.domain.Pageable pageable);
}
