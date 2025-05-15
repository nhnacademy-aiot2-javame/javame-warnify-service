package com.nhnacademy.javamewarnifyservice.warnfiy.service;

import com.nhnacademy.javamewarnifyservice.warnfiy.dto.WarnifyResponse;

import java.util.List;

/**
 * Warnify Service Interface.
 */
public interface WarnifyService {

    WarnifyResponse registerWarnfiy(String companyDomain, String warnInfo);

    List<WarnifyResponse> getWarnifyList(String companyDomain);

}
