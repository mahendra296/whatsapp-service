package com.whatsapp.service;

import com.whatsapp.enumclass.RedisKeyPrefix;
import com.whatsapp.enumclass.WhatsappMessageDataLabelType;
import com.whatsapp.interfaces.WhatsappPageManager;
import com.whatsapp.repository.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class WhatsappMessageService {

    private final RedisRepository redisRepository;
    private static final Logger log = LoggerFactory.getLogger(WhatsappPageManager.class);

    public String getCustomerCurrentPage(String msisdn, String country) {
        // construct the redis set's key
        var redisKey = RedisKeyPrefix.SESSION.getValue() + country + ":" + msisdn;
        var pageLabel = redisRepository.getValue(redisKey);
        if (pageLabel != null) {
            log.info("Fetched cached customer current index data from redis : {}", pageLabel);
            return pageLabel.toString();
        } return null;
    }

    public void saveCustomerCurrentPage(String msisdn, String country, String pageLabel) {
        // construct the redis set's key
        var redisKey = RedisKeyPrefix.SESSION.getValue() + country + ":" + msisdn;
        // save the session's current index label
        redisRepository.setValue(redisKey, pageLabel);
        log.info("Cached customer index label into Redis as {}", pageLabel);
    }

    public void clearCustomerCurrentPage(String msisdn, String country) {
        // construct the redis set's key
        var redisKey = RedisKeyPrefix.SESSION.getValue() + country + ":" + msisdn;
        // clear the session's current index label
        redisRepository.clearValue(redisKey);
    }

    public String getCustomerFormDataField(String msisdn, String country, String dataLabel) {
        // get the Text input from Redis
        return this.getCustomerFormDataField(msisdn, country, dataLabel, WhatsappMessageDataLabelType.TEXT);
    }

    /**
     * Get the collected customer's data from the USSD input
     * field for their session. This retrieves the data from their
     * Redis Hash (the HMSet key and fieldKey)
     *
     * @param msisdn        - the customer's phone number
     * @param country       - the customer's country
     * @param dataLabel     - the label to fetch the value
     * @param dataLabelType - the data type of the data saved in the label
     *                      from within the customer's redis Hash
     */
    public String getCustomerFormDataField(String msisdn, String country, String dataLabel, WhatsappMessageDataLabelType dataLabelType) {
        // construct the redis set's key
        var redisKey = RedisKeyPrefix.COLLECTED_DATA.getValue() + country + ":" + msisdn;
        // fetch the collected input data
        var result = redisRepository.getHashField(redisKey, dataLabel);
        if (result != null && !result.isBlank()) {
            return getDataTableTypeWiseValue(result, dataLabelType);
        }

        return (Objects.equals(result, "null")) ? null : result;
    }

    public void saveCustomerFormDataField(String msisdn, String country, String dataLabel, String dataValue) {
        // construct the redis set's key
        var redisKey = RedisKeyPrefix.COLLECTED_DATA.getValue() + country + ":" + msisdn;
        // save the collected customer input value to Redis
        redisRepository.saveHashField(redisKey, dataLabel, dataValue);
        log.info("Cached the customer form field with key : {} into Redis", dataLabel);
    }

    public void clearCustomerFormDataField(String msisdn, String country, String dataLabel) {
        // construct the redis set's key
        var redisKey = RedisKeyPrefix.COLLECTED_DATA.getValue() + country + ":" + msisdn;
        // removed the collected customer form data input from Redis
        redisRepository.deleteHashField(redisKey, dataLabel);
        log.info("Cleared the cached customer form field with key: {} from Redis", dataLabel);
    }

    public void clearCustomerFormDataFields(String msisdn, String country) {
        // construct the redis set's key
        var redisKey = RedisKeyPrefix.COLLECTED_DATA.getValue() + country + ":" + msisdn;
        // removed the collected customer form data input from Redis
        redisRepository.deleteHash(redisKey);
    }

    public void saveCustomerFormData(String msisdn, String country, String action, Map<String, String> dataValues) {
        // construct the redis set's key
        var redisKey = RedisKeyPrefix.SESSION.getValue() + country + ":" + msisdn + ":" + action;
        // save the session's current index label
        redisRepository.saveHashValues(redisKey, dataValues);
    }

    public Map<String,String> getCustomerFormData(String msisdn, String country, String action) {
        // construct the redis set's key
        var redisKey = RedisKeyPrefix.SESSION.getValue() + country + ":" + msisdn + ":" + action;
        return redisRepository.getHashValues(redisKey);
    }

    public Map<String,String> getCustomerFormDataFields(String msisdn, String country) {
        // fetch the form fields and return them here
        return this.getCustomerFormDataFields(msisdn, country, WhatsappMessageDataLabelType.TEXT);
    }

    public Map<String, String> getCustomerFormDataFields(String msisdn, String country, WhatsappMessageDataLabelType dataLabelType) {
        // construct the redis set's key
        var redisKey = RedisKeyPrefix.COLLECTED_DATA.getValue() + country + ":" + msisdn;
        // fetch ALL the collected input data
        AtomicReference<String> currentValue = null;
        Map<String, String> updatedResultMap = new HashMap<>();
        var resultMap = redisRepository.getHashValues(redisKey);
        if (resultMap == null) {
            resultMap = updatedResultMap;
        }
        resultMap.forEach((key, value) -> {
            currentValue.set(value);
            if(currentValue.get() != "null") {
                currentValue.set(getDataTableTypeWiseValue(currentValue.get(), dataLabelType));
            }
            if (!key.isBlank()) {
                updatedResultMap.put(key, currentValue.get());
            }
        });
        return updatedResultMap;
    }

    public  String getDataTableTypeWiseValue(String result, WhatsappMessageDataLabelType dataLabelType) {
        var resultList = result.split("|");
        var value = switch (dataLabelType) {
            case TEXT ->resultList[0];
            case DOCUMENT ->resultList[1];
            case URL ->resultList[2];
            default -> null;
        };
        return value;
    }
}
