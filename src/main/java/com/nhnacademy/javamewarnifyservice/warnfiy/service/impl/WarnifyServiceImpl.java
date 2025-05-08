package com.nhnacademy.javamewarnifyservice.warnfiy.service.impl;

import com.nhnacademy.javamewarnifyservice.warnfiy.domain.Warnify;
import com.nhnacademy.javamewarnifyservice.warnfiy.dto.WarnifyResponse;
import com.nhnacademy.javamewarnifyservice.warnfiy.repository.WarnifyRepository;
import com.nhnacademy.javamewarnifyservice.warnfiy.service.WarnifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class WarnifyServiceImpl implements WarnifyService {

    /**
     * warnifyRepository.
     */
    private final WarnifyRepository warnifyRepository;

    @Override
    public WarnifyResponse registerWarnfiy(String companyDomain, String warnInfo) {
        Warnify warnify = new Warnify(
                warnInfo,
                LocalDateTime.now(),
                companyDomain
        );

        Warnify savedWarnify = warnifyRepository.save(warnify);

        return new WarnifyResponse(
                savedWarnify.getWarnifyId(),
                savedWarnify.getCompanyDomain(),
                savedWarnify.getWarnInfo(),
                savedWarnify.getWarnDate()
        );
    }

    @Transactional(readOnly = true)
    @Override
    public List<WarnifyResponse> getWarnifyList(String companyDomain) {

        List<Warnify> warnifyList = warnifyRepository.findByCompanyDomain(companyDomain);

        List<WarnifyResponse> warnifyResponseList = new ArrayList<>();
        for (Warnify warnify : warnifyList) {
            warnifyResponseList.add(new WarnifyResponse(
                    warnify.getWarnifyId(),
                    warnify.getCompanyDomain(),
                    warnify.getWarnInfo(),
                    warnify.getWarnDate()
            ));
        }
        return warnifyResponseList;
    }
}