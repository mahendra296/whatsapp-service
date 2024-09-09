package com.whatsapp.enumclass;

public enum Api {
    VERSION("v3"),
    WHATSAPP_HEADER("WHATSAPP"),
    USSD_HEADER("USSD"),
    App("App");

    private final String value;

    Api(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
