package com.nhnacademy.javamewarnifyservice.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KSTTime {

    public static LocalDateTime convert(LocalDateTime localDateTime){
        return localDateTime.atZone(ZoneId.of("UTC"))
                .withZoneSameInstant(ZoneId.of("Asia/Seoul"))
                .toLocalDateTime();
    }

    public static LocalDateTime kstTimeNow(){
        LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("UTC"));
        return convert(localDateTime);
    }

}
