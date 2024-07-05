package com.whatsapp.interfaces.event;

import com.whatsapp.dto.response.ProcessWhatsappMessageResponse;
import com.whatsapp.dto.response.WhatsappMessage;

public interface IPageEvent {

    String getName();
    ProcessWhatsappMessageResponse processPageEvent(String msisdn, String country, WhatsappMessage message);
}
