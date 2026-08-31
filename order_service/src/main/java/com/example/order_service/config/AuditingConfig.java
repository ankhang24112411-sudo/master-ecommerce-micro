package com.example.order_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class AuditingConfig {
    @Bean
    public AuditorAware<String> auditorProvider() {

        return new AuditorAware<String>() {

            @Override
            public Optional<String> getCurrentAuditor() {

                // Tích hợp với Spring Security để lấy ra USER
                return Optional.of("huan.nguyen");
            }
        };
    }
}
