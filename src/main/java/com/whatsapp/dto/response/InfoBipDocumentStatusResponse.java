package com.whatsapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InfoBipDocumentStatusResponse {
    private Integer groupId;
    private String groupName;
    private Integer id;
    private String name;
    private String description;
}
