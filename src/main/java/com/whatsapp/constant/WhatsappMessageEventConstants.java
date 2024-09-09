package com.whatsapp.constant;

public class WhatsappMessageEventConstants {

    public static final String SERVICE_UNAVAILABLE = "Service is currently unavailable";
    public static final String THANK_YOU_FOR_CHOOSING_ABC = "Thank you for choosing ABC";
    public static final String FURTHER_QUERY_TEXT = "*For any further queries, contact us through:*\n\n";
    public static final String CONTACT_US_TEXT_BW = FURTHER_QUERY_TEXT +
            "Whatsapp on ${BW_WHATSAPP}\n" +
            "Email ID ${BW_EMAIL_ID}\n" +
            "Call us on ${BW_CALL_NUMBER}\n\n";
    public static final String END_MESSAGE = "*Thank you* \uD83D\uDC4B \n\n" +
            "*for using Letshego WhatsApp services, you will now be logged out.*\n\n" +
            "In order to start a new session, please send the word *\"Hi\"* and you will be sent an " +
            "*OTP* to verify your new session.\n\n";

    public static final String ERROR_OCCURRED = "An error occurred while processing your input ";
    public static final String WRONG_OPTION_SELECTED = "*The option you entered is incorrect.* \n\n" +
            "Kindly note that you have entered a wrong option, provide the right option to " +
            "continue with the loan process.";
}
