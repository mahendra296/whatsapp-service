package com.whatsapp.dto.response;

import com.whatsapp.enumclass.WhatsappMediaType;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageObject {
    private String message;
    private String mediaUrl;
    private WhatsappMediaType mediaType = WhatsappMediaType.TEXT;
    private boolean shouldPreviewUrl = false;
    private List<String> actionButtons;
    private InteractiveListMessage interactiveListMessage;
    private String footer;

    public MessageObject(String message) {
        this.message = message;
    }
}
