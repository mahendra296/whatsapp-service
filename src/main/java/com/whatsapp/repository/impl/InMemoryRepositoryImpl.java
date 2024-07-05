package com.whatsapp.repository.impl;

import com.whatsapp.repository.RedisRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository("inMemoryRepo")
@ConditionalOnProperty(name = "app.redis.enable", havingValue = "false", matchIfMissing = true)
public class InMemoryRepositoryImpl implements RedisRepository {

    private final Map<String, Object> valueStore = new ConcurrentHashMap<>();
    private final Map<String, Map<String, String>> hashStore = new ConcurrentHashMap<>();

    @Override
    public Object getValue(String key) {
        return valueStore.get(key);
    }

    @Override
    public void setValue(String key, String value) {
        valueStore.put(key, value);
    }

    @Override
    public void clearValue(String key) {
        valueStore.remove(key);
    }

    @Override
    public void saveHashField(String key, String fieldKey, String value) {
        hashStore.computeIfAbsent(key, k -> new ConcurrentHashMap<>()).put(fieldKey, value);
    }

    @Override
    public String getHashField(String key, String fieldKey) {
        Map<String, String> hash = hashStore.get(key);
        return hash != null ? hash.get(fieldKey) : null;
    }

    @Override
    public void saveHashValues(String key, Map<String, String> valuesMap) {
        hashStore.computeIfAbsent(key, k -> new ConcurrentHashMap<>()).putAll(valuesMap);
    }

    @Override
    public Map<String, String> getHashValues(String key) {
        Map<String, String> hash = hashStore.get(key);
        return hash != null ? new HashMap<>(hash) : new HashMap<>();
    }

    @Override
    public void deleteHashField(String key, String fieldKey) {
        Map<String, String> hash = hashStore.get(key);
        if (hash != null) {
            hash.remove(fieldKey);
        }
    }

    @Override
    public void deleteHash(String key) {
        hashStore.remove(key);
    }
}
