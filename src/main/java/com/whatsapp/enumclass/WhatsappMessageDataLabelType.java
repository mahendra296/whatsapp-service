package com.whatsapp.enumclass;

public enum WhatsappMessageDataLabelType {
    TEXT("TEXT"),
    URL("URL"),
    DOCUMENT("DOCUMENT");

    private final String value;

    WhatsappMessageDataLabelType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
