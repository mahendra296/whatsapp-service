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
public class WhatsappInteractiveButtonAction {
    private List<Button> buttons;
    private String title;
    private List<Section> sections;

    public WhatsappInteractiveButtonAction(List<Section> sections, String title) {
        this.sections = sections;
        this.title = title;
    }
}
