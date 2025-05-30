package com.nhnacademy.javamewarnifyservice.warnfiy.repository;

import com.nhnacademy.javamewarnifyservice.warnfiy.domain.Warnify;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Warnify 레포지토리.
 */
public interface WarnifyRepository extends JpaRepository<Warnify, Long> {
    Page<Warnify> findByCompanyDomain(String companyDomain, Pageable pageable);

    Optional<Warnify> findByWarnifyId(Long warnifyId);
}
