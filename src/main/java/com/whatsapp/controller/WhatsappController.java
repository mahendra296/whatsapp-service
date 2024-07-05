package com.whatsapp.controller;

import com.whatsapp.config.CountrySettingsConfig;
import com.whatsapp.constant.AppConstants;
import com.whatsapp.dto.request.*;
import com.whatsapp.dto.response.InfoBipIncomingWhatsappResponse;
import com.whatsapp.dto.response.InfoBipOutgoingWhatsappResponse;
import com.whatsapp.dto.response.MessageObject;
import com.whatsapp.dto.response.ProcessWhatsappMessageResponse;
import com.whatsapp.entity.APIResult;
import com.whatsapp.enumclass.Country;
import com.whatsapp.enumclass.WhatsappMediaType;
import com.whatsapp.interfaces.HandleWhatsappEventProvider;
import com.whatsapp.interfaces.infobip.InfoBipWhatsappManagerAdapter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class WhatsappController {

    private final HandleWhatsappEventProvider handleWhatsappEventProvider;
    private final InfoBipWhatsappManagerAdapter infobipWhatsappManagerAdapter;
    private final CountrySettingsConfig countrySettingsConfig;
    @Value("${whatsapp.app-base-url}")
    private String appBaseUrl;
    @Value("${whatsapp.infobipApiKey}")
    private String infoBipApiKey;
    @Value("${whatsapp.infobip.sms.delay}")
    private Long infoSendSmsDelayInSeconds;
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
                try {
                    if (message.getActionButtons() != null && !message.getActionButtons().isEmpty()) {
                        var infobipOutgoingButtonOutgoingDto =
                                prepareWhatsappButtonMessage(message, country, processPageResponse);
                        log.info("Sending interactive button message to Infobip: EventsType: {}", infobipOutgoingButtonOutgoingDto);
                        sendInteractiveButtonMessage(infobipOutgoingButtonOutgoingDto);
                        if (message.getMediaUrl() != null) {
                            TimeUnit.MILLISECONDS.sleep(infoSendSmsDelayInSeconds);
                        }
                    } else if (message.getMediaType() == WhatsappMediaType.INTERACTIVE_LIST_REPLY) {
                        var infobipInteractiveListReplyOutgoingDto =
                                prepareInteractiveListMessage(message, country, processPageResponse);
                        log.info("Sending interactive button message to Infobip: {}", infobipInteractiveListReplyOutgoingDto);
                        sendInteractiveListMessage(infobipInteractiveListReplyOutgoingDto);
                        TimeUnit.MILLISECONDS.sleep(infoSendSmsDelayInSeconds);
                    } else if (message.getMediaType() == WhatsappMediaType.DOCUMENT) {
                        var infoBipDocumentOutgoingDto =
                                prepareDocumentMessage(message, country, processPageResponse);
                        log.info("Sending document message to InfoBip: {}", infoBipDocumentOutgoingDto);
                        sendDocumentMessage(infoBipDocumentOutgoingDto);
                        TimeUnit.MILLISECONDS.sleep(infoSendSmsDelayInSeconds);
                    } else {
                        var whatsappOutgoingMessage = prepareOutMessage(message);
                        log.info("\n\nSending to Infobip: EventsType : {} || Message: \n {}\n", whatsappEventType, message.getMessage());
                        sendToInfobip(country, processPageResponse, whatsappOutgoingMessage);
                        TimeUnit.MILLISECONDS.sleep(infoSendSmsDelayInSeconds);
                    }
                } catch (Exception ex) {
                    log.error("Error while send whatsapp message", ex);
                }
            });
        } catch (Exception exception) {
            //we catch here to continue for other input objects
            log.error("Exception occurred : ${exception.errorMsg} for input: $input", exception);
        }
    }

    private WhatsApp prepareOutMessage(MessageObject processedMsg) {
        WhatsApp outMsg = new WhatsApp();
        String url = appBaseUrl + processedMsg.getMediaUrl();
        switch (processedMsg.getMediaType()) {
            case IMAGE:
                outMsg.setImageUrl(url);
                break;
            case AUDIO:
                outMsg.setAudioUrl(url);
                break;
            case VIDEO:
                outMsg.setVideoUrl(url);
                break;
            case DOCUMENT:
                outMsg.setFileUrl(url);
                break;
            default:
                log.info("For {}, it's not required to set URL", processedMsg.getMediaType());
                break;
        }
        outMsg.setText(processedMsg.getMessage());
        return outMsg;
    }

    private void sendDocumentMessage(InfoBipDocumentOutGoingDto infoBipDocumentOutgoingDto) {
        infobipWhatsappManagerAdapter.sendDocumentMessage(
                infoBipApiKey,
                infoBipDocumentOutgoingDto
        );
    }

    private InfoBipInteractiveOutGoingDto prepareInteractiveListMessage(MessageObject processedMsg, String country, ProcessWhatsappMessageResponse processPageResponse) {
        if (processedMsg.getInteractiveListMessage() == null || processedMsg.getInteractiveListMessage().getListSections().isEmpty()) {
            throw new RuntimeException("Error");
        }

        List<Section> sections = processedMsg.getInteractiveListMessage().getListSections().stream()
                .map(section -> new Section(
                        section.getTitle(),
                        section.getRows().stream()
                                .map(row -> new Row(
                                        generateButtonId(row.getTitle()),
                                        trimListTittleTo24Chars(row.getTitle()),
                                        row.getDescription()
                                ))
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());

        Content content = new Content(
                new WhatsappInteractiveButtonMessageBody(processedMsg.getMessage()),
                new WhatsappInteractiveButtonAction(sections, processedMsg.getInteractiveListMessage().getTitle()),
                null,
                null
        );

        var countrySettings = countrySettingsConfig.getCountries().get(country);
        String from = countrySettings != null ? countrySettings.getPhoneNumber() : null;

        return new InfoBipInteractiveOutGoingDto(
                from != null ? from : "",
                processPageResponse.getMsisdn(),
                null,
                content
        );
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

    public InfoBipInteractiveOutGoingDto prepareWhatsappButtonMessage(MessageObject processedMsg, String country, ProcessWhatsappMessageResponse processPageResponse) {
        WhatsappButtonsMessageHeader header = prepareInteractiveButtonMessageHeader(processedMsg);
        List<Button> buttons = processedMsg.getActionButtons().stream()
                .map(it -> new Button(generateButtonId(it), it))
                .collect(Collectors.toList());
        Content content = new Content(
                new WhatsappInteractiveButtonMessageBody(processedMsg.getMessage()),
                new WhatsappInteractiveButtonAction(buttons, null, null),
                header,
                null
        );
        String from = countrySettingsConfig.getCountries().get(country).getPhoneNumber();
        return new InfoBipInteractiveOutGoingDto(
                from,
                processPageResponse.getMsisdn(),
                null,
                content
        );
    }

    public void sendInteractiveButtonMessage(InfoBipInteractiveOutGoingDto infobipInteractiveOutGoingdto) {
        APIResult<InfoBipOutgoingWhatsappResponse> result = infobipWhatsappManagerAdapter.sendInteractiveButtonMessage(
                infoBipApiKey,
                infobipInteractiveOutGoingdto
        );

        // Handle the result as needed
        if (result.isSuccess()) {
            InfoBipOutgoingWhatsappResponse response = result.getData();
        } else {
            String errorMessage = result.getMessage();
        }
    }
    public void sendInteractiveListMessage(InfoBipInteractiveOutGoingDto infobipInteractiveOutGoingdto) {
        infobipWhatsappManagerAdapter.sendInteractiveListMessage(
                infoBipApiKey,
                infobipInteractiveOutGoingdto
        );
    }


    public static String generateButtonId(String buttonText) {
        return buttonText.toUpperCase(Locale.ENGLISH).replaceAll("\\s", "_");
    }

    private String trimListTittleTo24Chars(String title) {
        if (title.length() > 24) {
            return title.substring(0, 20) + "...";
        }
        return title;
    }
    public WhatsappButtonsMessageHeader prepareInteractiveButtonMessageHeader(MessageObject processedMsg) {
        String url = appBaseUrl + processedMsg.getMediaUrl();

        switch (processedMsg.getMediaType()) {
            case IMAGE:
            case AUDIO:
            case VIDEO:
            case DOCUMENT:
                return new WhatsappButtonsMessageHeader(
                        processedMsg.getMediaType(),
                        null, // text
                        url, // mediaUrl
                        null // filename
                );
            default:
                return null;
        }
    }
    public InfoBipDocumentOutGoingDto prepareDocumentMessage(
            MessageObject processedMsg,
            String country,
            ProcessWhatsappMessageResponse processPageResponse) {

        String mediaUrl = processedMsg.getMediaUrl();
        String filename = processedMsg.getMessage();
        String caption = processedMsg.getMessage();

        var countrySettings = countrySettingsConfig.getCountries().get(country);
        String from = countrySettings != null ? countrySettings.getPhoneNumber() : null;

        return new InfoBipDocumentOutGoingDto(
                from,
                processPageResponse.getMsisdn(),
                null,
                new DocumentContent(mediaUrl, filename, caption)
        );
    }

    public void sendToInfobip(
            String country,
            ProcessWhatsappMessageResponse processPageResponse,
            WhatsApp whatsappOutgoingMessage
    ) {
        String scenarioKey = countrySettingsConfig.getCountries().get(country).getScenarioKey();
        if (scenarioKey == null || scenarioKey.isEmpty()) {
            scenarioKey = null;
        }

        Destination destination = new Destination(new To(processPageResponse.getMsisdn()));
        List<Destination> destinationsList = Collections.singletonList(destination);

        InfoBipOutgoingWhatsappDto infobipOutgoingDto = new InfoBipOutgoingWhatsappDto(
                scenarioKey,
                destinationsList,
                whatsappOutgoingMessage
        );

        infobipWhatsappManagerAdapter.sendMessage(infoBipApiKey, infobipOutgoingDto);
    }
}
