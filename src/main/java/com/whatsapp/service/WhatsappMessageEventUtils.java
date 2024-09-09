package com.whatsapp.service;

public class WhatsappMessageEventUtils {

    public static void moveToMessage(
            WhatsappMessageService whatsappMessageService,
            String msisdn,
            String country,
            String messageLabel
    ) {
        whatsappMessageService.saveCustomerCurrentPage(msisdn, country, messageLabel);
    }
}
