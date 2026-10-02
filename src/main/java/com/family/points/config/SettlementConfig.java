package com.family.points.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Clock;
import java.time.ZoneId;

/**
 * 结算月份与结算时间统一使用北京时间
 */
@Configuration
public class SettlementConfig {

    @Bean
    public Clock settlementClock() {
        return Clock.system(ZoneId.of("Asia/Shanghai"));
    }
}
