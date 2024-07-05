package com.whatsapp.client;

import com.whatsapp.dto.request.InfoBipDocumentOutGoingDto;
import com.whatsapp.dto.request.InfoBipInteractiveOutGoingDto;
import com.whatsapp.dto.request.InfoBipOutgoingWhatsappDto;
import com.whatsapp.dto.response.InfoBipDocumentResponseDto;
import com.whatsapp.dto.response.InfoBipOutgoingWhatsappResponse;
import com.whatsapp.entity.APIResult;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "whatsappManager",
        url = "${info-bip-whatsapp.url}"
)
public interface InfoBipWhatsappManagerClient {
    /**
     * To handle sending messages to Infobip's Whatsapp
     * Omni API to deliver Whatsapp messages to customer's devices
     *
     * @param // infobipApiKey - the Infobip API key for the request
     * @param // infobipOutgoingWhatsappDto - the DTO for the whatsapp request
     * @return [APIResult<InfobipOutgoingWhatsappResponse>] - the response from whatsapp API
     */
    @RequestLine("POST /omni/1/advanced")
    @Headers("Content-Type: application/json")
    APIResult<InfoBipOutgoingWhatsappResponse> sendMessage(
            @RequestHeader("Authorization") String infobipApiKey,
            InfoBipOutgoingWhatsappDto infoBipOutgoingWhatsappDto
    );

    /**
     * To handle fetching media files uploaded to Infobip's Whatsapp
     * This media will be uploaded directly to AWS S3 bucket
     *
     * @param // infobipApiKey - the Infobip API key for the request
     * @param // whatsappSenderId - the Whatsapp sender's Id
     * @param // mediaId - the Whatsapp mediaId
     * @return [Response] - the feign response object containing the media with HTML
     */
    @RequestLine("GET /whatsapp/1/senders/{whatsappSenderId}/media/{mediaId}")
    @Headers("Content-Type: application/json")
    Response fetchMedia(
            @RequestHeader("Authorization") String infobipApiKey,
            @Param("whatsappSenderId") String whatsappSenderId,
            @Param("mediaId") String mediaId
    );

    /**
     * To handle fetching media files uploaded to Infobip's Whatsapp
     * This media will be uploaded directly to AWS S3 bucket
     *
     * @param infobipApiKey - the Infobip API key for the request
     * @param whatsappSenderId - the Whatsapp sender's Id
     * @param mediaId - the Whatsapp mediaId
     * @return [byte[]] - the feign response object containing the media
     */
    @RequestLine("GET /whatsapp/1/senders/{whatsappSenderId}/media/{mediaId}")
    @Headers("Content-Type: application/json")
    byte[] fetchMediaAsByte(
            @RequestHeader("Authorization") String infobipApiKey,
            @Param("whatsappSenderId") String whatsappSenderId,
            @Param("mediaId") String mediaId
    );

    @RequestLine("POST /whatsapp/1/message/interactive/buttons")
    @Headers("Content-Type: application/json")
    APIResult<InfoBipOutgoingWhatsappResponse> sendInteractiveButtonMessage(
            @RequestHeader("Authorization") String infobipApiKey,
            InfoBipInteractiveOutGoingDto infoBipOutgoingWhatsappDto
    );

    @RequestLine("POST /whatsapp/1/message/interactive/list")
    @Headers("Content-Type: application/json")
    APIResult<InfoBipOutgoingWhatsappResponse> sendInteractiveListMessage(
            @RequestHeader("Authorization") String infobipApiKey,
            InfoBipInteractiveOutGoingDto infoBipOutgoingWhatsappDto
    );

    @RequestLine("POST /whatsapp/1/message/document")
    @Headers("Content-Type: application/json")
    APIResult<InfoBipDocumentResponseDto> sendDocumentMessage(
            @RequestHeader("Authorization") String infobipApiKey,
            InfoBipDocumentOutGoingDto infoBipDocumentOutGoingDto
    );
}
