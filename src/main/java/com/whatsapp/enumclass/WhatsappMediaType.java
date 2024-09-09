package com.whatsapp.enumclass;

public enum WhatsappMediaType {
    NONE("NONE"),
    TEXT("TEXT"),
    IMAGE("IMAGE"),
    AUDIO("AUDIO"),
    VOICE("VOICE"),
    VIDEO("VIDEO"),
    LOCATION("LOCATION"),
    DOCUMENT("DOCUMENT"),
    STICKER("STICKER"),
    CONTACT("CONTACT"),
    BUTTON("BUTTON"),
    INTERACTIVE_BUTTON_REPLY("INTERACTIVE_BUTTON_REPLY"),
    INTERACTIVE_LIST_REPLY("INTERACTIVE_LIST_REPLY"),
    ORDER("ORDER"),
    UNSUPPORTED("UNSUPPORTED");

    private final String value;

    WhatsappMediaType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}


