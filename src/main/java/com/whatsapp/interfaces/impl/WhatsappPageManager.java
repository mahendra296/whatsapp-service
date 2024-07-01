package com.whatsapp.interfaces.impl;

import com.whatsapp.dto.response.ProcessWhatsappMessageResponse;
import com.whatsapp.enumclass.WhatsappMediaType;
import com.whatsapp.interfaces.HandleWhatsappEventProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WhatsappPageManager implements HandleWhatsappEventProvider {
    @Override
    public ProcessWhatsappMessageResponse processWhatsappRequest(String msisdn, String country, String whatsappMessageId, String inputMessage, String inputButtonClick, WhatsappMediaType inputMessageType, String inputMediaUrl, String whatsappEvent) {
        return null;
    }
}
