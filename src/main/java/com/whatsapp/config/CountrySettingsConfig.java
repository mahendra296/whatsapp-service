package com.whatsapp.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "whatsapp.settings")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CountrySettingsConfig {
    private Map<String, CountryConfigs> countries;
}
