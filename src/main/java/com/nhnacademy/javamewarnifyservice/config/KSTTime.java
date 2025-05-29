package com.nhnacademy.javamewarnifyservice.config;

import java.time.LocalDateTime;
import java.time.ZoneId;

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
