package com.whatsapp.dto.response;

import com.whatsapp.enumclass.WhatsappMessageType;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Builder.Default
    private Map<String, String> buttonActions = Collections.emptyMap();

    @Builder.Default
    private Map<String, String> inputActions = Collections.emptyMap();

    @Builder.Default
    private Set<String> validInputs = Collections.emptySet();

    private boolean isDynamicInputActions;
    private String backWhatsappMessageLabel;
    private boolean isTerminal;
    private String nextPageLabel;

    @Builder.Default
    private Map<String, String> metadata = Collections.emptyMap();

    private boolean saveInputWithOptionType;
}
