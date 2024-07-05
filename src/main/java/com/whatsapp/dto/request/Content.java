package com.whatsapp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Content {
    private WhatsappInteractiveButtonMessageBody body;
    private WhatsappInteractiveButtonAction action;
    private WhatsappButtonsMessageHeader header;
    private WhatsappButtonsMessageFooter footer;
}
