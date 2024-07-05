package com.whatsapp.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CountryConfigs {
    private String phoneNumber;
    private String startKeywords;
    private String defaultTerminateMessage;
    private String scenarioKey;
}
