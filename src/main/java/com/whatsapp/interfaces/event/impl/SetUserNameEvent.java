package com.whatsapp.interfaces.event.impl;

import com.whatsapp.constant.WhatsappMessageDataLabels;
import com.whatsapp.constant.WhatsappMessageLabels;
import com.whatsapp.dto.response.MessageObject;
import com.whatsapp.dto.response.ProcessWhatsappMessageResponse;
import com.whatsapp.dto.response.WhatsappMessage;
import com.whatsapp.interfaces.event.IPageEvent;
import com.whatsapp.service.WhatsappMessageEventUtils;
import com.whatsapp.service.WhatsappMessageService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SetUserNameEvent implements IPageEvent {

    private static final Logger log = LoggerFactory.getLogger(SetUserNameEvent.class);
    private final WhatsappMessageService whatsappMessageService;

    @Override
    public String getName() {
        return "setUserNameEvent";
    }

    @Override
    public ProcessWhatsappMessageResponse processPageEvent(String msisdn, String country, WhatsappMessage message) {
        try {
            log.info("Invoke setUserNameEvent event for msisdn : {} and country : {}", msisdn, country);
            String userName = whatsappMessageService.getCustomerFormDataField(
                    msisdn, country, WhatsappMessageDataLabels.PREFERRED_CUSTOMER_NAME);

            // Replace placeholder in all messages in the list
            for (MessageObject messageItem : message.getMessages()) {
                String messageText = messageItem.getMessage();
                if (messageText != null && messageText.contains("{{preferredCustomerName}}")) {
                    messageText =
                            messageText.replace("{{preferredCustomerName}}", (userName != null) ? userName : "Dummy");
                    messageItem.setMessage(messageText);
                }
            }
            return new ProcessWhatsappMessageResponse(msisdn, message.getMessages(), false);
        } catch (Exception e) {
            log.error("Error processing page event for msisdn: {} and country: {}", msisdn, country, e);
            WhatsappMessageEventUtils.moveToMessage(
                    whatsappMessageService, msisdn, country, WhatsappMessageLabels.GENERIC_ERROR_END_MESSAGE);
            return null;
        }
    }
}
