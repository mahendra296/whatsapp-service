package com.whatsapp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InfoBipDocumentOutGoingDto {
    private String from;
    private String to;
    private String messageId;
    private DocumentContent content;
}
