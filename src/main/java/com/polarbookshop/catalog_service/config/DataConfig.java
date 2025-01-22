package com.polarbookshop.catalog_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class DataConfig {

    /**
     * 현재 인증된 사용자를 반환
     * @return
     */
    @Bean
    AuditorAware<String> auditorAware() {
        return () -> Optional.ofNullable(SecurityContextHolder.getContext())    // SecurityContext 객체 추출
                .map(SecurityContext::getAuthentication)                        // Authentication 객체 추출
                .filter(Authentication::isAuthenticated)                        //
                .map(Authentication::getName);                                  // 현재 인증된 유저명 추출
    }
}
