package com.whatsapp.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@ConfigurationProperties(prefix = "whatsapp.settings")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class CountrySettingsConfig {
    private Map<String, CountryConfigs> countries;
}
