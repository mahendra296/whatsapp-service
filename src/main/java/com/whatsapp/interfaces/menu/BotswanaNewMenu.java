package com.whatsapp.interfaces.menu;

import com.whatsapp.constant.WhatsappMessageDataLabels;
import com.whatsapp.constant.WhatsappMessageEventConstants;
import com.whatsapp.constant.WhatsappMessageLabels;
import com.whatsapp.dto.response.MessageObject;
import com.whatsapp.dto.response.WhatsappMessage;
import com.whatsapp.enumclass.WhatsappMediaType;
import com.whatsapp.enumclass.WhatsappMessageType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BotswanaNewMenu implements IMenu {

    private HashMap<String, WhatsappMessage> menuMap = new HashMap<>();
    @Override
    public HashMap<String, WhatsappMessage> getMenuMap() {
        menuMap.put(WhatsappMessageLabels.WELCOME_MESSAGE_NWC, getWelcomeMessageWithEnterName());
        menuMap.put(WhatsappMessageLabels.BEGIN_SESSION_EVENTS, getBeginSession());
        menuMap.put(WhatsappMessageLabels.WELCOME_MESSAGE, getWelcomeMessage());

        return menuMap;
    }

    @Override
    public WhatsappMessage getMenuWhatsappMessage(String messageLabel) {
        return menuMap.get(messageLabel);
    }

    private WhatsappMessage getBeginSession(){
        var whatsappMessage = new WhatsappMessage();

        MessageObject messageObject = new MessageObject();
        messageObject.setMediaType(WhatsappMediaType.TEXT);
        messageObject.setMessage("Welcome to Letshego");

        whatsappMessage.setType(WhatsappMessageType.INPUT);
        whatsappMessage.setEvents("CheckCustomerConsent");
        whatsappMessage.setDataLabel("beginEntryPoint");
        return whatsappMessage;
    }

    private WhatsappMessage getWelcomeMessage(){
        var whatsappMessage = WhatsappMessage.builder()
                .messages(List.of(MessageObject.builder()
                        .mediaType(WhatsappMediaType.TEXT)
                        .message("*Hi!*\nWhat is your name?")
                        .build()))
                .type(WhatsappMessageType.INPUT)
                .events("inputDataValidation,goToNextMessage,searchCustomerInDB")
                .dataLabel(WhatsappMessageDataLabels.PREFERRED_CUSTOMER_NAME)
                .build();
        return whatsappMessage;
    }
    private WhatsappMessage getWelcomeMessageWithEnterName(){
        Map<String, String> buttonActionMap = new HashMap<>();
        buttonActionMap.put("SKIP", WhatsappMessageLabels.WELCOME_MESSAGE_WITH_GREETINGS_FOR_NC);
        buttonActionMap.put("EXIT", WhatsappMessageLabels.END_MESSAGE);
        var whatsappMessage = WhatsappMessage.builder()
                .messages(List.of(MessageObject.builder()
                        .mediaType(WhatsappMediaType.INTERACTIVE_BUTTON_REPLY)
                        .message("*Hi!*\nWhat is your name?")
                        .actionButtons(List.of("Skip", "Exit"))
                        .build()))
                .buttonActions(buttonActionMap)
                .type(WhatsappMessageType.INPUT)
                .events("inputDataValidation,goToNextMessage,searchCustomerInDB")
                .dataLabel(WhatsappMessageDataLabels.PREFERRED_CUSTOMER_NAME)
                .build();
        return whatsappMessage;
    }

    private WhatsappMessage getGenericErrorEndMenu() {
        var whatsappMessage = new WhatsappMessage();

        List<MessageObject> messages = new ArrayList<>();

        MessageObject messageObject = new MessageObject();
        MessageObject messageObject1 = new MessageObject();
        messageObject.setMediaType(WhatsappMediaType.TEXT);
        messageObject.setMessage("*Dear valued customer* \n" +
                "We seem to have encountered a technical issue \n\n" +
                WhatsappMessageEventConstants.CONTACT_US_TEXT_BW);

        messageObject1.setMediaType(WhatsappMediaType.TEXT);
        messageObject1.setMessage(WhatsappMessageEventConstants.END_MESSAGE);
        messages.add(messageObject);
        messages.add(messageObject1);

        whatsappMessage.setMessages(messages);
        whatsappMessage.setEvents("events");
        whatsappMessage.setType(WhatsappMessageType.DIALOG);
        whatsappMessage.setTerminal(true);

        return whatsappMessage;
    }

}
