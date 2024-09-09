package com.whatsapp.interfaces;

import com.whatsapp.constant.WhatsappMessageEventConstants;
import com.whatsapp.constant.WhatsappMessageLabels;
import com.whatsapp.dto.response.MessageObject;
import com.whatsapp.dto.response.ProcessWhatsappMessageResponse;
import com.whatsapp.dto.response.WhatsappMessage;
import com.whatsapp.enumclass.WhatsappMediaType;
import com.whatsapp.enumclass.WhatsappMessageEventType;
import com.whatsapp.enumclass.WhatsappMessageType;
import com.whatsapp.interfaces.event.IPageEvent;
import com.whatsapp.interfaces.event.impl.ClearCustomerDataEvent;
import com.whatsapp.interfaces.menu.BotswanaNewMenu;
import com.whatsapp.service.WhatsappMessageEventUtils;
import com.whatsapp.service.WhatsappMessageService;
import feign.FeignException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;

@Component
@RequiredArgsConstructor
public class WhatsappPageManager implements HandleWhatsappEventProvider {

    private final List<IPageEvent> events;
    private final BotswanaNewMenu botswanaNewMenu;
    private final WhatsappMessageService whatsappMessageService;
    private final ClearCustomerDataEvent clearCustomerDataEvent;
    private static final Logger log = LoggerFactory.getLogger(WhatsappPageManager.class);

    private HashMap<String, WhatsappMessage> botswanaWhatsappMenuMap = new HashMap<>();
    private final HashMap<String, IPageEvent> eventMap = new HashMap();

    @PostConstruct
    public void init() {
        botswanaWhatsappMenuMap = botswanaNewMenu.getMenuMap();
        loadEvents();
    }

    private void loadEvents() {
        // load the Whatsapp messages events here
        log.info("Loading the Whatsapp messages events into memory");
        events.stream().forEach(event -> {
            eventMap.put(event.getName(), event);
        });
        log.info("Finished loading the Whatsapp messages events into memory");
    }

    public WhatsappMessage getWhatsappMenuMessage(String country, String messageLabel) {
        // Return the menu for the customer's country
        switch (country) {
            case SupportedCountries.INDIA:
                return botswanaWhatsappMenuMap.get(messageLabel);
            default:
                return null;
        }
    }

    @Override
    public ProcessWhatsappMessageResponse processWhatsappRequest(String msisdn, String country, String whatsappMessageId, String inputMessage, String inputButtonClick, WhatsappMediaType inputMessageType, String inputMediaUrl, String whatsappEvent) {
        if (Objects.equals(inputMessage, "####")) {
            clearCustomerDataEvent.processPageEvent(msisdn, country, new WhatsappMessage());
            MessageObject messageObject = new MessageObject();
            messageObject.setMessage("Session cleared.");
            return new ProcessWhatsappMessageResponse(msisdn, List.of(messageObject), false);
        }
        String messageLabel = whatsappMessageService.getCustomerCurrentPage(msisdn, country);
        if (StringUtils.isEmpty(messageLabel)) {
            // handle the start event here
            return this.handleWhatsappRequestStartEvent(msisdn, country, messageLabel, inputMessage, inputButtonClick, inputMessageType, inputMediaUrl);
        }
        // must be a continue event
        return this.handleWhatsappRequestContinueEvent(msisdn, country, messageLabel, inputMessage, inputButtonClick, inputMessageType, inputMediaUrl);
    }

    public ProcessWhatsappMessageResponse handleWhatsappRequestStartEvent(String msisdn, String country, String messageLabel, String whatsappInputMessage, String inputButtonClick, WhatsappMediaType inputMessageType, String inputMediaUrl) {

        String startPageLabel = getCustomerStartMenu(msisdn, country);
        String preEventMessageLabel = startPageLabel;

        // continue the processing algorithm here
        WhatsappMessage whatsappMessage = getWhatsappMenuMessage(country, startPageLabel);
        if (whatsappMessage == null) {
            return serviceUnavailableMessage(msisdn);
        }

        // fire the menu index pre-input events here
        ProcessWhatsappMessageResponse whatsappMessageResponse = fireMessageEvents(msisdn, country, whatsappMessage, WhatsappMessageEventType.PRE_INPUT);
        if (whatsappMessageResponse != null) {
            return whatsappMessageResponse;
        }

        // any update to current index flow ?
        startPageLabel = whatsappMessageService.getCustomerCurrentPage(msisdn, country);
        // if the message label has been modified, return the new page messages here
        if (!preEventMessageLabel.equals(startPageLabel)) {
            whatsappMessage = getWhatsappMenuMessage(country, startPageLabel);
            if (whatsappMessage == null) {
                return serviceUnavailableMessage(msisdn);
            }
//        return getResponseForNextPage(msisdn, country, startPageLabel);
        }

        // if for some reasons this is a terminal index, do Redis clean up
        if (whatsappMessage.isTerminal()) {
            clearCustomerDataEvent.processPageEvent(msisdn, country, whatsappMessage); // fire index event
        }

        // return the first/start index
        return new ProcessWhatsappMessageResponse(msisdn, whatsappMessage.getMessages(), whatsappMessage.isTerminal());
    }

    public String getCustomerStartMenu(String msisdn, String country) {
        // get the begin session Whatsapp messages
        WhatsappMessage beginSessionEventsWhatsappMessage = (WhatsappMessage) this.getWhatsappMenuMessage(
                country,
                WhatsappMessageLabels.BEGIN_SESSION_EVENTS
        );

        // fire the begin session events
        fireMessageEvents(
                msisdn, country,
                beginSessionEventsWhatsappMessage,
                WhatsappMessageEventType.POST_INPUT
        );

        // correct start menu
        return whatsappMessageService.getCustomerCurrentPage(msisdn, country);
    }

    public ProcessWhatsappMessageResponse serviceUnavailableMessage(String msisdn) {
        log.info("Service is currently unavailable");
        List<MessageObject> messages = new ArrayList<>();
        MessageObject messageObject = new MessageObject();
        messageObject.setMessage(WhatsappMessageEventConstants.SERVICE_UNAVAILABLE + ". " +
                WhatsappMessageEventConstants.THANK_YOU_FOR_CHOOSING_ABC);
        messages.add(messageObject);

        return new ProcessWhatsappMessageResponse(msisdn, messages, true);
    }

    public ProcessWhatsappMessageResponse fireMessageEvents(
            String msisdn,
            String country,
            WhatsappMessage whatsappMessage,
            WhatsappMessageEventType eventType) {

        // fire the menu index events here
        List<String> pageEventNames = new ArrayList<>();
        String eventListStr = (eventType == WhatsappMessageEventType.POST_INPUT) ?
                whatsappMessage.getEvents() : whatsappMessage.getPreInputEvents();

        if (eventListStr != null && !eventListStr.isEmpty()) {
            pageEventNames = Arrays.asList(eventListStr.split(","));
        }

        IPageEvent eventObject;
        ProcessWhatsappMessageResponse eventResponse = null;
        for (String eventName : pageEventNames) {
            // get event
            eventObject = eventMap.get(eventName);
            if (eventObject != null) {
                log.info("Firing the Whatsapp messages event with name: " + eventObject.getName());

                try {
                    eventResponse = eventObject.processPageEvent(msisdn, country, whatsappMessage);
                } catch (FeignException e) {
                    log.error("An error has occurred in API call", e);
                    log.info("Method : " + e.request().httpMethod() +
                            " Request Url : " + e.request().url() +
                            "\nstatus : " + e.status() +
                            "\nmessage : " + e.getMessage());

                    WhatsappMessageEventUtils.moveToMessage(
                            whatsappMessageService,
                            msisdn,
                            country,
                            WhatsappMessageLabels.GENERIC_ERROR_END_MESSAGE
                    );
                    return null;
                }
            }

            if (eventResponse != null) {
                log.info("Fired Whatsapp messages " + eventType.getValue() + " event '" +
                        (eventObject != null ? eventObject.getName() : "") +
                        "' returned a response: " + eventResponse);
                // terminate and return the error/messages to the customer
                return eventResponse;
            }
        }
        // no event terminated the whatsapp menu flow
        return null;
    }

    private ProcessWhatsappMessageResponse handleWhatsappRequestContinueEvent(
            String msisdn,
            String country,
            String passedMessageLabel,
            String whatsappInputMessage,
            String inputButtonClick,
            WhatsappMediaType inputMessageType,
            String inputMediaUrl
    ) {
        String messageLabel = passedMessageLabel;

        // continue the processing algorithm here
        WhatsappMessage whatsappMessage = getWhatsappMenuMessage(country, messageLabel);
        if (whatsappMessage == null) {
            return errorOccurredMessage(msisdn, messageLabel);
        }

        // handle buttonActions here
        String currentMessageLabel = whatsappMessageService.getCustomerCurrentPage(msisdn, country);
        if (whatsappMessage.getButtonActions().containsKey(inputButtonClick)) {
            messageLabel = whatsappMessage.getButtonActions().get(inputButtonClick);
            if (whatsappMessage.getEvents() != null && messageLabel != null && whatsappMessage.getDataLabel() != null) {
                whatsappMessageService.saveCustomerFormDataField(msisdn, country, whatsappMessage.getDataLabel(), inputButtonClick);
                ProcessWhatsappMessageResponse fireMessageEvents = this.fireMessageEvents(msisdn, country, whatsappMessage, WhatsappMessageEventType.POST_INPUT);
                if (fireMessageEvents != null) {
                    return fireMessageEvents;
                }
            }
            return getResponseForNextPage(msisdn, country, messageLabel);
        }

        log.info("[" + msisdn + "]User has entered the input: " + whatsappInputMessage);

        // handle inputActions here
        if (whatsappMessage.getInputActions().containsKey(whatsappInputMessage.toUpperCase()) && !whatsappMessage.isDynamicInputActions()) {
            messageLabel = whatsappMessage.getInputActions().get(whatsappInputMessage.toUpperCase());
            if (whatsappMessage.getEvents() != null && messageLabel != null && whatsappMessage.getDataLabel() != null) {
                whatsappMessageService.saveCustomerFormDataField(msisdn, country, whatsappMessage.getDataLabel(), whatsappInputMessage);
                this.fireMessageEvents(msisdn, country, whatsappMessage, WhatsappMessageEventType.POST_INPUT);
            }
            if (whatsappMessage.isSaveInputWithOptionType()) {
                if (whatsappMessage.getDataLabel() != null) {
                    whatsappMessageService.saveCustomerFormDataField(msisdn, country, whatsappMessage.getDataLabel(), whatsappInputMessage + "|" + inputMessageType + "|" + inputMediaUrl);
                }
            }
            return getResponseForNextPage(msisdn, country, messageLabel);
        }

        // handle input message stage and save in Redis
        if (whatsappMessage.getType() == WhatsappMessageType.INPUT) {
            if (!whatsappMessage.getValidInputs().isEmpty() && !whatsappMessage.getValidInputs().contains(whatsappInputMessage)) {
                return wrongOptionSelectedMessage(msisdn);
            }
        }

        // fire POST_INPUT events
        ProcessWhatsappMessageResponse whatsappMessageResponse = this.fireMessageEvents(msisdn, country, whatsappMessage, WhatsappMessageEventType.POST_INPUT);
        if (whatsappMessageResponse != null) {
            return whatsappMessageResponse;
        }

        // save the next page
        if (whatsappMessage.getNextPageLabel() != null) {
            log.info("Changing current page using nextPageLabel config from : " + whatsappMessageService.getCustomerCurrentPage(msisdn, country) + " to " + whatsappMessage.getNextPageLabel());
            whatsappMessageService.saveCustomerCurrentPage(msisdn, country, whatsappMessage.getNextPageLabel());
        }

        // update current index flow
        messageLabel = whatsappMessageService.getCustomerCurrentPage(msisdn, country);

        // fetch and return next index in the menu flow
        return getResponseForNextPage(msisdn, country, messageLabel);
    }

    private ProcessWhatsappMessageResponse getResponseForNextPage(
            String msisdn,
            String country,
            String nextMessageLabel
    ) {
        // preEvent message label
        String nextMessageLabelStr = nextMessageLabel;
        String preEventMessageLabel = nextMessageLabel;

        // fetch next index from the menu list
        WhatsappMessage nextWhatsappMessage = getWhatsappMenuMessage(country, nextMessageLabelStr);
        if (nextWhatsappMessage == null) {
            return errorOccurredMessage(msisdn, nextMessageLabelStr);
        }

        // move the customer's Whatsapp session to next message index
        whatsappMessageService.saveCustomerCurrentPage(msisdn, country, nextMessageLabelStr);

        // fire the pre-events for this message stage here
        ProcessWhatsappMessageResponse whatsappMessageResponse = fireMessageEvents(msisdn, country, nextWhatsappMessage, WhatsappMessageEventType.PRE_INPUT);
        // if a preInput event terminated the flow, return the response here
        if (whatsappMessageResponse != null) {
            return whatsappMessageResponse;
        }

        // any update to current index flow ?
        nextMessageLabelStr = whatsappMessageService.getCustomerCurrentPage(msisdn, country);
        // if the message label has been modified, return the new page messages here
        if (!Objects.equals(preEventMessageLabel, nextMessageLabelStr)) {
            return getResponseForNextPage(msisdn, country, nextMessageLabelStr);
        }

        // if the next index is a terminate index, fire the events for that index
        if (nextWhatsappMessage.isTerminal()) {
            ProcessWhatsappMessageResponse terminatePageResponse = fireMessageEvents(msisdn, country, nextWhatsappMessage, WhatsappMessageEventType.POST_INPUT);
            if (terminatePageResponse != null) {
                return terminatePageResponse;
            }
            // clear collected user data from Redis
            clearCustomerDataEvent.processPageEvent(msisdn, country, nextWhatsappMessage); // fire index event
        }

        // should return the next index
        return new ProcessWhatsappMessageResponse(
                msisdn,
                nextWhatsappMessage.getMessages(),
                nextWhatsappMessage.isTerminal()
        );
    }

    public ProcessWhatsappMessageResponse wrongOptionSelectedMessage(
            String msisdn
    ) {
        log.info("Entered wrong option from Whatsapp messages options menu");
        return new ProcessWhatsappMessageResponse(
                msisdn,
                Collections.singletonList(new MessageObject(WhatsappMessageEventConstants.WRONG_OPTION_SELECTED)),
                true
        );
    }

    public ProcessWhatsappMessageResponse errorOccurredMessage(
            String msisdn,
            String label
    ) {
        log.warn("An error occurred while processing the input, unable to find the menu: " + label);
        return new ProcessWhatsappMessageResponse(
                msisdn,
                Collections.singletonList(new MessageObject(WhatsappMessageEventConstants.ERROR_OCCURRED)),
                true
        );
    }

    private static class SupportedCountries {
        public static final String BOTSWANA = "Botswana";

        public static final String INDIA = "india";
        public static final String NIGERIA = "Nigeria";
        public static final String KENYA = "Kenya";
        public static final String LESOTHO = "Lesotho";
        public static final String UGANDA = "Uganda";
        public static final String ESWATINI = "Eswatini";
        public static final String GHANA = "Ghana";
        public static final String TANZANIA = "Tanzania";
    }
}

