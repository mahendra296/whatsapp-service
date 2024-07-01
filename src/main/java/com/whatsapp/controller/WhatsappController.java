package com.whatsapp.controller;

import com.whatsapp.constant.AppConstants;
import com.whatsapp.dto.request.InfoBipIncomingWhatsappDTO;
import com.whatsapp.dto.request.Result;
import com.whatsapp.dto.response.InfoBipIncomingWhatsappResponse;
import com.whatsapp.enumclass.Country;
import com.whatsapp.enumclass.WhatsappMediaType;
import com.whatsapp.interfaces.HandleWhatsappEventProvider;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class WhatsappController {

    private final HandleWhatsappEventProvider handleWhatsappEventProvider;
    private static final Logger log =   LoggerFactory.getLogger(WhatsappController.class);
    static final int COUNTRY_CODE_LENGTH = 3;
    static final String COUNTRY_CODE_PREFIX = "+";
    List<WhatsappMediaType> mediaTypes = Arrays.asList(
            WhatsappMediaType.INTERACTIVE_BUTTON_REPLY,
            WhatsappMediaType.INTERACTIVE_LIST_REPLY
    );

    @PostMapping("/webhook/infobip-whatsapp/incoming-message")
    public InfoBipIncomingWhatsappResponse handleIncomingWhatsappMessage(@RequestBody InfoBipIncomingWhatsappDTO request) {
        log.info("Request details dto request : ", request.toString());

        InfoBipIncomingWhatsappResponse response = new InfoBipIncomingWhatsappResponse("success");

        for (Result input : request.getResults()) {
            var mediaType = input.getMessage().getType();
            var inputMessage = getInputMessage(input, mediaType);
            processInputMessage(
                    input,
                    inputMessage,
                    mediaType
            );
        }

        return response;
    }

    private String getInputMessage(Result result, WhatsappMediaType inputMessageType) {
        var inputMessage = result.getMessage().getText();
        // if the Message Type is not a TEXT, then set the Input Message as the media Caption
        if (inputMessageType != WhatsappMediaType.TEXT && result.getMessage().getCaption() != null) {
            inputMessage = result.getMessage().getCaption();
        }
        return inputMessage;
    }

    private void processInputMessage(Result input, String inputMessage, WhatsappMediaType mediaType) {
        try {
            //extracting country code and country name
            var code = COUNTRY_CODE_PREFIX + input.getFrom().substring(COUNTRY_CODE_LENGTH);
            var country = Country.fromPhoneCode(code).name();

            var whatsappEventType = getEventType(country, inputMessage);
            var inputButtonClick = "";

            if (mediaTypes.contains(mediaType) && input.getMessage().getId() != null) {
                inputButtonClick = input.getMessage().getId();
                log.info("User :: {} has clicked INTERACTIVE_BUTTON :: {}", input.getFrom(), inputButtonClick);
            }

            var processPageResponse = handleWhatsappEventProvider.processWhatsappRequest(
                    input.getFrom(),
                    country,
                    input.getMessageId(),
                    inputMessage,
                    inputButtonClick,
                    mediaType,
                    input.getMessage().getUrl(),
                    whatsappEventType
            );

            // based on the return from the process, send response to phone
            processPageResponse.getMessages().forEach(message -> {
                /*try {
                    val sendInteractiveButtonFeatureFlag = flagProvider.isFeatureEnabled(
                            "SEND_NEW_MENU_MESSAGE",
                            Country.valueOf(country)
                    )
                    if (!it.actionButtons.isNullOrEmpty() && sendInteractiveButtonFeatureFlag) {
                        val infobipOutgoingButtonOutgoingDto =
                                prepareWhatsappButtonMessage(it, country, processPageResponse)
                        log.info("Sending interactive button message to Infobip: EventsType:  $infobipOutgoingButtonOutgoingDto")
                        sendInteractiveButtonMessage(infobipOutgoingButtonOutgoingDto)
                        if (it.mediaUrl != null) {
                            TimeUnit.MILLISECONDS.sleep(infoSendSmsDelayInSeconds)
                        }
                    } else if (it.mediaType == WhatsappMediaType.INTERACTIVE_LIST_REPLY) {
                        val infobipInteractiveListReplyOutgoingDto =
                                prepareInteractiveListMessage(it, country, processPageResponse)
                        log.info("Sending interactive button message to Infobip: $infobipInteractiveListReplyOutgoingDto")
                        sendInteractiveListMessage(infobipInteractiveListReplyOutgoingDto)
                        TimeUnit.MILLISECONDS.sleep(infoSendSmsDelayInSeconds)
                    } else if (it.mediaType == WhatsappMediaType.DOCUMENT && LOAN_DOCUMENTS.contains(it.message)) {
                        val infoBipDocumentOutgoingDto =
                                prepareDocumentMessage(it, country, processPageResponse)
                        log.info("Sending document message to InfoBip: $infoBipDocumentOutgoingDto")
                        sendDocumentMessage(infoBipDocumentOutgoingDto)
                        TimeUnit.MILLISECONDS.sleep(infoSendSmsDelayInSeconds)
                    } else {
                        val whatsappOutgoingMessage = prepareOutMessage(it)
                        log.info("\n\nSending to Infobip: EventsType:  $whatsappEventType || Message: \n${it.message}\n")
                        sendToInfobip(country, processPageResponse, whatsappOutgoingMessage)
                        TimeUnit.MILLISECONDS.sleep(infoSendSmsDelayInSeconds)
                    }
                } catch (Exception ex) {
                    log.error("Error while send whatsapp message", ex)
                }*/
            });
        } catch (Exception exception) {
            //we catch here to continue for other input objects
            log.error("Exception occurred : ${exception.errorMsg} for input: $input", exception);
        }
    }

    private String getEventType(String country, String inputMessage) {
        var whatsappEventType = AppConstants.CONTINUE_EVENT;
        if (getStartKeywords(country).contains(inputMessage.trim().toLowerCase())) {
            whatsappEventType = AppConstants.START_EVENT;
        }
        return whatsappEventType;
    }

    private List<String> getStartKeywords(String country) {
        var startKeywords = "hi,hello,hey";
        return List.of(startKeywords.trim().toLowerCase().split(","));
    }
}
