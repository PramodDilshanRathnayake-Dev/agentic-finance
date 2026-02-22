package com.agenticfinance.repository;

import com.agenticfinance.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, String> {

    List<Order> findByPortfolioIdOrderByCreatedAtDesc(String portfolioId, org.springframework.data.domain.Pageable pageable);
}
