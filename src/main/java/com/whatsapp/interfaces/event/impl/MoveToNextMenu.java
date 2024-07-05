package com.whatsapp.interfaces.event.impl;

import com.whatsapp.dto.response.ProcessWhatsappMessageResponse;
import com.whatsapp.dto.response.WhatsappMessage;
import com.whatsapp.interfaces.WhatsappPageManager;
import com.whatsapp.interfaces.event.IPageEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MoveToNextMenu implements IPageEvent {

    private static final Logger log = LoggerFactory.getLogger(WhatsappPageManager.class);
    @Override
    public String getName() {
        return "moveToNextMenu";
    }

    @Override
    public ProcessWhatsappMessageResponse processPageEvent(String msisdn, String country, WhatsappMessage message) {
        log.info("Invoke moveToNextMenu event for msisdn : {} and country : {}", msisdn, country);
        return null;
    }
}
