package com.whatsapp.dto.request;

import com.whatsapp.enumclass.WhatsappMediaType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WhatsappButtonsMessageHeader {
    private WhatsappMediaType type;
    private String text;
    private String mediaUrl;
    private String filename;
}
