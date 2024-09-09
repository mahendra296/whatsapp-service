package com.whatsapp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InfoBipIncomingWhatsappDTO {
        private List<Result> results;
        private int messageCount;
        private int pendingMessageCount;
}
