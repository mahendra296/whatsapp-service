package com.whatsapp.client;

import com.whatsapp.dto.request.InfoBipDocumentOutGoingDto;
import com.whatsapp.dto.request.InfoBipInteractiveOutGoingDto;
import com.whatsapp.dto.request.InfoBipOutgoingWhatsappDto;
import com.whatsapp.dto.response.InfoBipDocumentResponseDto;
import com.whatsapp.dto.response.InfoBipOutgoingWhatsappResponse;
import com.whatsapp.entity.APIResult;
import feign.Param;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "whatsappManager",
        url = "${whatsapp.infobip.url}"
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
    @GetMapping("/omni/1/advanced")
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
    /*@GetMapping("/whatsapp/1/senders/{whatsappSenderId}/media/{mediaId}")
    Response fetchMedia(
            @RequestHeader("Authorization") String infobipApiKey,
            @RequestParam("whatsappSenderId") String whatsappSenderId,
            @RequestParam("mediaId") String mediaId
    );*/

    /**
     * To handle fetching media files uploaded to Infobip's Whatsapp
     * This media will be uploaded directly to AWS S3 bucket
     *
     * @param infobipApiKey    - the Infobip API key for the request
     * @param whatsappSenderId - the Whatsapp sender's Id
     * @param mediaId          - the Whatsapp mediaId
     * @return [byte[]] - the feign response object containing the media
     */
    /*@GetMapping("/whatsapp/1/senders/{whatsappSenderId}/media/{mediaId}")
    byte[] fetchMediaAsByte(
            @RequestHeader("Authorization") String infobipApiKey,
            @Param("whatsappSenderId") String whatsappSenderId,
            @Param("mediaId") String mediaId
    );*/

    @PostMapping("/whatsapp/1/message/interactive/buttons")
    APIResult<InfoBipOutgoingWhatsappResponse> sendInteractiveButtonMessage(
            @RequestHeader("Authorization") String infobipApiKey,
            InfoBipInteractiveOutGoingDto infoBipOutgoingWhatsappDto
    );

    @PostMapping("/whatsapp/1/message/interactive/list")
    APIResult<InfoBipOutgoingWhatsappResponse> sendInteractiveListMessage(
            @RequestHeader("Authorization") String infobipApiKey,
            InfoBipInteractiveOutGoingDto infoBipOutgoingWhatsappDto
    );

    @PostMapping("/whatsapp/1/message/document")
    APIResult<InfoBipDocumentResponseDto> sendDocumentMessage(
            @RequestHeader("Authorization") String infobipApiKey,
            InfoBipDocumentOutGoingDto infoBipDocumentOutGoingDto
    );
}
