package com.nhnacademy.javamewarnifyservice.warnfiy.service.impl;

import com.nhnacademy.javamewarnifyservice.advice.exception.WarnifyNotFoundException;
import com.nhnacademy.javamewarnifyservice.config.KSTTime;
import com.nhnacademy.javamewarnifyservice.warnfiy.domain.Warnify;
import com.nhnacademy.javamewarnifyservice.warnfiy.dto.WarnifyResponse;
import com.nhnacademy.javamewarnifyservice.warnfiy.repository.WarnifyRepository;
import com.nhnacademy.javamewarnifyservice.warnfiy.service.WarnifyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Warnify 서비스.
 */
@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class WarnifyServiceImpl implements WarnifyService {

    /**
     * warnifyRepository.
     */
    private final WarnifyRepository warnifyRepository;

    /**
     * Warnify DB에 정보 등록.
     * @param companyDomain 회사 도메인
     * @param warnInfo 경고 정보
     * @return Warnfiy 응답 정보.
     */
    @Override
    public WarnifyResponse registerWarnfiy(String companyDomain, String warnInfo) {
        Warnify warnify = new Warnify(
                warnInfo,
                KSTTime.kstTimeNow(),
                companyDomain
        );
        Warnify savedWarnify = warnifyRepository.save(warnify);

        return new WarnifyResponse(
                savedWarnify.getWarnifyId(),
                savedWarnify.getCompanyDomain(),
                savedWarnify.getWarnInfo(),
                savedWarnify.getWarnDate(),
                savedWarnify.isWarnResolve() ? "해결" : "미해결"
        );
    }

    /**
     * Warnif DB에서 가지고오기.
     * @param companyDomain 회사정보
     * @return Warnfiy 응답 정보.
     */
    @Override
    public Page<WarnifyResponse> getWarnifyList(String companyDomain, Pageable pageable) {
        Page<Warnify> warnifyList = warnifyRepository.findByCompanyDomain(companyDomain, pageable);
        List<WarnifyResponse> warnifyResponseList = new ArrayList<>();
        for (Warnify warnify : warnifyList) {
            warnifyResponseList.add(new WarnifyResponse(
                    warnify.getWarnifyId(),
                    warnify.getCompanyDomain(),
                    warnify.getWarnInfo(),
                    warnify.getWarnDate(),
                    warnify.isWarnResolve() ? "해결" : "미해결"
            ));
        }
        return new PageImpl<>(warnifyResponseList, pageable, warnifyList.getTotalElements());
    }

    /**
     * 경고를 해결 했으면 true를 받아서 resolve칼럼 true로 변경.
     * @param resolve true or false
     * @return true or false
     */
    @Override
    public String resolveWarn(Long warnifyId, String resolve) {
        //resolve의 null 여부
        if(Objects.isNull(resolve)){
            throw new IllegalArgumentException("Null값은 안됩니다. true or false를 입력해주세요");
        }

        // true, false 잘 들어왔는지
        String checkResolve = resolve.toLowerCase();
        if(!(checkResolve.equalsIgnoreCase("true") || checkResolve.equalsIgnoreCase("false"))){
            throw new IllegalArgumentException("잘못된 값입니다. true or false를 입력해주세요");
        }

        Warnify warnify = warnifyRepository.findByWarnifyId(warnifyId).orElseThrow(
                () -> new WarnifyNotFoundException("경로를 찾을 수 없습니다.")
        );

        boolean booleanResolve;
        // String resolve -> boolean resolve 변환
        if(resolve.equalsIgnoreCase("true")) {
            booleanResolve = true;
            warnify.updateResolve(booleanResolve);
            return "경고에 대한 문제해결이 되었습니다.";
        }
        warnify.updateResolve(false);
        return "경고에 대한 문제해결이 안됬습니다.";
    }
}