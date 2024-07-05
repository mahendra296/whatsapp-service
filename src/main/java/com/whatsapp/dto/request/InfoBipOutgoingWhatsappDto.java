package com.whatsapp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InfoBipOutgoingWhatsappDto {
    private String scenarioKey;
    private List<Destination> destinations;
    private WhatsApp whatsApp;
    private Sms sms;

    public InfoBipOutgoingWhatsappDto(String scenarioKey, List<Destination> destinationsList, WhatsApp whatsappOutgoingMessage) {
        this.scenarioKey = scenarioKey;
        this.destinations = destinationsList;
        this.whatsApp = whatsappOutgoingMessage;
    }
}
