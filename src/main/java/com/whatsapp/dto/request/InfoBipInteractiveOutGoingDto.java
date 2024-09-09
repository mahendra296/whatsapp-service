package com.whatsapp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InfoBipInteractiveOutGoingDto {
    private String from;
    private String to;
    private String messageId;
    private Content content;
}
