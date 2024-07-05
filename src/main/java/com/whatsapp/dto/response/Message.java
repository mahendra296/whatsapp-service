package com.whatsapp.dto.response;

import com.whatsapp.dto.request.To;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private To to;
    private Status status;
    private String messageId;
}
