package com.whatsapp.enumclass;

public enum WhatsappMessageEventType {
    PRE_INPUT("PRE_INPUT"),
    POST_INPUT("POST_INPUT");

    private final String value;

    WhatsappMessageEventType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}


