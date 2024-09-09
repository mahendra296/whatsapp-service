package com.whatsapp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Button {
    private String type;
    private String id;
    private String title;

    public Button(String id, String title) {
        this.id = id;
        this.title = title;
    }
}
