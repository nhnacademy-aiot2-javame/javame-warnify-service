package com.nhnacademy.javamewarnifyservice.warnfiy.repository;

import com.nhnacademy.javamewarnifyservice.warnfiy.domain.Warnify;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Warnify 레포지토리.
 */
public interface WarnifyRepository extends JpaRepository<Warnify, Long> {
    List<Warnify> findByCompanyDomain(String companyDomain);
}
