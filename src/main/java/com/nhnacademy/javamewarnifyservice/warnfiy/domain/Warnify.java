package com.nhnacademy.javamewarnifyservice.warnfiy.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DB에 저장될 도메인
 * 경고 발생시 회사도메인, 일자, 경고정보 저장.
 */
@Entity
@Table(name = "warnifys")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Warnify {

    /**
     * 경고 번호.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long warnifyId;

    /**
     * 경고 정보.
     */
    @Column(name = "warn_info", nullable = false)
    private String warnInfo;

    /**
     * 경고 발생 일자.
     */
    @Column(name = "warn_date", nullable = false)
    private LocalDateTime warnDate;

    /**
     * pk된 회사.
     */
    @Column(name = "company_domain")
    private String companyDomain;

    /**
     * Warnify 생성자.
     * @param warnInfo 경고 정보
     * @param warnDate 경고 일자
     * @param companyDomain 회사 도메인
     */
    public Warnify(String warnInfo, LocalDateTime warnDate, String companyDomain) {
        this.warnInfo = warnInfo;
        this.warnDate = warnDate;
        this.companyDomain = companyDomain;
    }

}
