package com.whatsapp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WhatsApp {
    private String text;
    private String imageUrl;
    private String audioUrl;
    private String videoUrl;
    private String fileUrl;
    private Boolean previewUrl;
}
