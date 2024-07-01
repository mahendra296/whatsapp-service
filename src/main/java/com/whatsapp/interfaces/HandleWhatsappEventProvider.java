package com.whatsapp.interfaces;

import com.whatsapp.dto.response.ProcessWhatsappMessageResponse;
import com.whatsapp.enumclass.WhatsappMediaType;

public interface HandleWhatsappEventProvider {
    ProcessWhatsappMessageResponse processWhatsappRequest(
            String msisdn,
            String country,
            String whatsappMessageId,
            String inputMessage,
            String inputButtonClick,
            WhatsappMediaType inputMessageType,
            String inputMediaUrl,
            String whatsappEvent
    );
}
