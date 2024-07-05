package com.whatsapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InfoBipDocumentResponseDto {
    private String to;
    private Integer messageCount;
    private String messageId;
    private InfoBipDocumentStatusResponse status;
}
