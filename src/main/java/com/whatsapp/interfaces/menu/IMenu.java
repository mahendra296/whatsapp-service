package com.whatsapp.interfaces.menu;

import com.whatsapp.dto.response.WhatsappMessage;

import java.util.HashMap;

public interface IMenu {

    HashMap<String, WhatsappMessage> getMenuMap();

    WhatsappMessage getMenuWhatsappMessage(String messageLabel);
}
