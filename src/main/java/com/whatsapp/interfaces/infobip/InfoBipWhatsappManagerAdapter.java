package com.whatsapp.interfaces.infobip;

import com.whatsapp.client.InfoBipWhatsappManagerClient;
import com.whatsapp.dto.request.InfoBipDocumentOutGoingDto;
import com.whatsapp.dto.request.InfoBipInteractiveOutGoingDto;
import com.whatsapp.dto.request.InfoBipOutgoingWhatsappDto;
import com.whatsapp.dto.response.InfoBipDocumentResponseDto;
import com.whatsapp.dto.response.InfoBipOutgoingWhatsappResponse;
import com.whatsapp.entity.APIResult;
import com.whatsapp.enumclass.Api;
import com.whatsapp.interfaces.WhatsappPageManager;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class InfoBipWhatsappManagerAdapter implements InfoBipWhatsappManagerProvider {

    private final InfoBipWhatsappManagerClient infoBipWhatsappManagerClient;

    @Override
    public APIResult<InfoBipOutgoingWhatsappResponse> sendMessage(String infobipApiKey, InfoBipOutgoingWhatsappDto infobipOutgoingWhatsappDto) {
        return infoBipWhatsappManagerClient.sendMessage(Api.App.getValue() + " " + infobipApiKey, infobipOutgoingWhatsappDto);
    }

    @Override
    public InputStream fetchMedia(String infobipApiKey, String whatsappSenderId, String mediaId) {
        InputStream inputStream = null;
        try {
            // Feign File download
            /*byte[] response = infoBipWhatsappManagerClient.fetchMediaAsByte(
                    Api.App.getValue() + " " + infobipApiKey,
                    whatsappSenderId,
                    mediaId
            );*/
            inputStream = new ByteArrayInputStream(null);

        } catch (Exception e) {
            // log.error("fetchMedia : ", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return inputStream;
    }

    @Override
    public APIResult<InfoBipOutgoingWhatsappResponse> sendInteractiveButtonMessage(String infobipApiKey, InfoBipInteractiveOutGoingDto infobipInteractiveOutGoingDto) {
        return infoBipWhatsappManagerClient.sendInteractiveButtonMessage(Api.App.getValue() + " " + infobipApiKey, infobipInteractiveOutGoingDto);
    }

    @Override
    public APIResult<InfoBipOutgoingWhatsappResponse> sendInteractiveListMessage(String infobipApiKey, InfoBipInteractiveOutGoingDto infobipInteractiveOutGoingDto) {
        return infoBipWhatsappManagerClient.sendInteractiveListMessage(Api.App.getValue() + " " + infobipApiKey, infobipInteractiveOutGoingDto);
    }

    @Override
    public APIResult<InfoBipDocumentResponseDto> sendDocumentMessage(String infobipApiKey, InfoBipDocumentOutGoingDto infoBipDocumentOutGoingDto) {
        return infoBipWhatsappManagerClient.sendDocumentMessage(Api.App.getValue() + " " + infobipApiKey, infoBipDocumentOutGoingDto);
    }
}
