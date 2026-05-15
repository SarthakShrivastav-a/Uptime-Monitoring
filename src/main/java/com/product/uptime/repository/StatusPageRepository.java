package com.product.uptime.repository;

import com.product.uptime.entity.StatusPage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StatusPageRepository extends JpaRepository<StatusPage, String> {
    List<StatusPage> findByUserId(String userId);
    Optional<StatusPage> findBySlug(String slug);
}
