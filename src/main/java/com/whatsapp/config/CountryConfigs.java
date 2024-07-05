package com.whatsapp.config;

import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CountryConfigs {
    private String phoneNumber;
    private String startKeywords;
    private String defaultTerminateMessage;
    private String scenarioKey;
}
