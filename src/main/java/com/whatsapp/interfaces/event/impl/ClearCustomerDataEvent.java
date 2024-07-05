package com.whatsapp.interfaces.event.impl;

import com.whatsapp.dto.response.ProcessWhatsappMessageResponse;
import com.whatsapp.dto.response.WhatsappMessage;
import com.whatsapp.interfaces.WhatsappPageManager;
import com.whatsapp.interfaces.event.IPageEvent;
import com.whatsapp.service.WhatsappMessageService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClearCustomerDataEvent implements IPageEvent {

    private static final Logger log = LoggerFactory.getLogger(WhatsappPageManager.class);
    private final WhatsappMessageService whatsappMessageService;
    @Override
    public String getName() {
        return "clearCustomerData";
    }

    @Override
    public ProcessWhatsappMessageResponse processPageEvent(String msisdn, String country, WhatsappMessage message) {
        log.info("Invoke clearCustomerData event for msisdn : {} and country : {}", msisdn, country);

        log.info("Clearing all the cached data for the customer from Redis");
        whatsappMessageService.clearCustomerCurrentPage(msisdn, country);
        whatsappMessageService.clearCustomerFormDataFields(msisdn, country);

        return null;
    }
}
