package com.nhnacademy.javamewarnifyservice.controller;

import com.nhnacademy.javamewarnifyservice.warnfiy.dto.WarnifyResponse;
import com.nhnacademy.javamewarnifyservice.warnfiy.service.WarnifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


/**
 * 경고 목록을 가지고오는 컨트롤러 입니다.
 */
@RestController
@RequestMapping(value = "/warnify")
@RequiredArgsConstructor
public class WarnifyController {

    /**
     * warnifyService 호출.
     */
    private final WarnifyService warnifyService;

    /**
     * 경고온 목록 페이지로 조회.
     * @param companyDomain 회사 도메인
     * @param pageable 페이지
     * @return 경고정보 페이지로 리턴.
     */
    @GetMapping("/list/{company-domain}")
    public ResponseEntity<Page<WarnifyResponse>> getWarnifyList(@PathVariable("company-domain")String companyDomain, @PageableDefault(size = 2, sort = "warnDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<WarnifyResponse> warnifyResponseList = warnifyService.getWarnifyList(companyDomain, pageable);

        return ResponseEntity.ok(warnifyResponseList);
    }

    @PatchMapping("/resolve/{warnifyId}")
    public ResponseEntity<Map<String, String>> warnifyResolve(@PathVariable("warnifyId") Long warnifyId, @RequestParam("resolve") String resolve){
        String info = warnifyService.resolveWarn(warnifyId, resolve);
        return ResponseEntity.ok(Map.of("result",info));
    }

}
