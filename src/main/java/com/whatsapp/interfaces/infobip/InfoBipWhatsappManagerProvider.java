package com.whatsapp.interfaces.infobip;

import com.whatsapp.dto.request.InfoBipDocumentOutGoingDto;
import com.whatsapp.dto.request.InfoBipInteractiveOutGoingDto;
import com.whatsapp.dto.request.InfoBipOutgoingWhatsappDto;
import com.whatsapp.dto.response.InfoBipDocumentResponseDto;
import com.whatsapp.dto.response.InfoBipOutgoingWhatsappResponse;
import com.whatsapp.entity.APIResult;

import java.io.InputStream;

public interface InfoBipWhatsappManagerProvider {
    APIResult<InfoBipOutgoingWhatsappResponse> sendMessage(String infobipApiKey, InfoBipOutgoingWhatsappDto infobipOutgoingWhatsappDto);

    InputStream fetchMedia(String infobipApiKey, String whatsappSenderId, String mediaId);

    APIResult<InfoBipOutgoingWhatsappResponse> sendInteractiveButtonMessage(String infobipApiKey, InfoBipInteractiveOutGoingDto infobipInteractiveOutGoingDto);

    APIResult<InfoBipOutgoingWhatsappResponse> sendInteractiveListMessage(String infobipApiKey, InfoBipInteractiveOutGoingDto infobipInteractiveOutGoingDto);

    APIResult<InfoBipDocumentResponseDto> sendDocumentMessage(String infobipApiKey, InfoBipDocumentOutGoingDto infoBipDocumentOutGoingDto);
}
