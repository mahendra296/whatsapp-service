package com.whatsapp.dto.response;

import com.whatsapp.enumclass.WhatsappMediaType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageObject {
    private String message;
    private String mediaUrl;
    private WhatsappMediaType mediaType = WhatsappMediaType.TEXT;
    private boolean shouldPreviewUrl;
    private List<String> actionButtons;
    private InteractiveListMessage interactiveListMessage;
    private String footer;

    public MessageObject(String message) {
        this.message = message;
    }
}
