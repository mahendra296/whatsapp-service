package com.whatsapp.dto.response;

import com.whatsapp.enumclass.WhatsappMessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WhatsappMessage {
    private int index;
    private List<MessageObject> messages;
    private WhatsappMessageType type;
    private String preInputEvents;
    private String events;
    private String dataLabel;
    private Map<String, String> buttonActions;
    private Map<String, String> inputActions;
    private Set<String> validInputs;
    private boolean isDynamicInputActions;
    private String backWhatsappMessageLabel;
    private boolean isTerminal;
    private String nextPageLabel;
    private Map<String, String> metadata;
    private boolean saveInputWithOptionType;
}
