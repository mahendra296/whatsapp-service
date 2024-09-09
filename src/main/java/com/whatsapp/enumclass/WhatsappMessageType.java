package com.whatsapp.enumclass;

public enum WhatsappMessageType {
    INPUT("INPUT"),
    TEMPLATE("TEMPLATE"),
    OPTION("OPTION"),
    DIALOG("DIALOG");

    private final String value;

    WhatsappMessageType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    // Optional: You can add a static method to retrieve enum from string value
    public static WhatsappMessageType fromString(String value) {
        for (WhatsappMessageType type : WhatsappMessageType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No constant with value " + value + " found");
    }
}
