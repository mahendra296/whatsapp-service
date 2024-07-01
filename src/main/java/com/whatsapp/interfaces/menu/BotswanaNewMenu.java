package com.whatsapp.interfaces.menu;

import com.whatsapp.constant.WhatsappMessageDataLabels;
import com.whatsapp.constant.WhatsappMessageLabels;
import com.whatsapp.dto.response.MessageObject;
import com.whatsapp.dto.response.WhatsappMessage;
import com.whatsapp.enumclass.WhatsappMediaType;
import com.whatsapp.enumclass.WhatsappMessageType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BotswanaNewMenu implements IMenu {

    private HashMap<String, WhatsappMessage> menuMap = new HashMap<>();
    @Override
    public HashMap<String, WhatsappMessage> getMenuMap() {
        menuMap.put(WhatsappMessageLabels.WELCOME_MESSAGE_NWC, getWelcomeMessageWithEnterName());
        return menuMap;
    }

    @Override
    public WhatsappMessage getMenuWhatsappMessage(String messageLabel) {
        return menuMap.get(messageLabel);
    }

    private WhatsappMessage getWelcomeMessageWithEnterName(){
        var whatsappMessage = new WhatsappMessage();

        MessageObject messageObject = new MessageObject();
        messageObject.setMediaType(WhatsappMediaType.INTERACTIVE_BUTTON_REPLY);
        messageObject.setMessage("*Hi!*\n" +
                "What is your name?");
        messageObject.setActionButtons(List.of("Skip", "Exit"));

        Map<String, String> buttonActionMap = new HashMap<>();
        buttonActionMap.put("SKIP", WhatsappMessageLabels.WELCOME_MESSAGE_WITH_GREETINGS_FOR_NC);
        buttonActionMap.put("EXIT", WhatsappMessageLabels.END_MESSAGE);
        whatsappMessage.setButtonActions(buttonActionMap);

        whatsappMessage.setType(WhatsappMessageType.INPUT);
        whatsappMessage.setEvents("inputDataValidation,goToNextMessage,searchCustomerInDB");
        whatsappMessage.setDataLabel(WhatsappMessageDataLabels.PREFERRED_CUSTOMER_NAME);
        return whatsappMessage;
    }
}
