package com.hof.wms.integration.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * SellFox API配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "sellfox")
public class SellFoxConfig {

    private String clientId;
    private String clientSecret;
    private String signKey;
    private String baseUrl;
}
