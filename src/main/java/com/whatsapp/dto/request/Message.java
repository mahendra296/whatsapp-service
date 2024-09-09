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
public class Message {
    private WhatsappMediaType type;
    private String text;
    private String caption;
    private String url;
    private String id;
    private String title;
    private String description;
}