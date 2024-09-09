package com.whatsapp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    private String from;
    private String to;
    private String integrationType;
    private String receivedAt;
    private String messageId;
    private String pairedMessageId;
    private String callbackData;
    private Message message;
    private Price price;
}

