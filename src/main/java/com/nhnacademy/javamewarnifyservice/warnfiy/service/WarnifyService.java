package com.nhnacademy.javamewarnifyservice.warnfiy.service;

import com.nhnacademy.javamewarnifyservice.warnfiy.dto.WarnifyResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


/**
 * Warnify Service Interface.
 */
public interface WarnifyService {

    WarnifyResponse registerWarnfiy(String companyDomain, String warnInfo);

    Page<WarnifyResponse> getWarnifyList(String companyDomain, Pageable pageable);

    String resolveWarn(Long warnifyId, String resolve);

}
