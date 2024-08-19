package com.polarbookshop.catalog_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter @Setter
@ConfigurationProperties(prefix = "polar") // polar로 시작하는 설정 속성에 대한 소스임을 표시
public class PolarProperties {

    // 사용자 정의 속성인 polar.greeting (프레픽스 + 필드명)
    // 속성이 문자열로 인식되는 필드
    private String greeting;
}
