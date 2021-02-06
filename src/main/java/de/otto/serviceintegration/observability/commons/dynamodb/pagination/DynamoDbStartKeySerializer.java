package de.otto.serviceintegration.observability.commons.dynamodb.pagination;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import de.otto.serviceintegration.observability.commons.dynamodb.pagination.jackson.AttributeValueDeserializer;
import de.otto.serviceintegration.observability.commons.dynamodb.pagination.jackson.AttributeValueSerializer;
import de.otto.serviceintegration.observability.commons.dynamodb.pagination.jackson.SdkBytesDeserializer;
import de.otto.serviceintegration.observability.commons.dynamodb.pagination.jackson.SdkBytesSerializer;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.io.IOException;
import java.util.Map;

@RequiredArgsConstructor
public class DynamoDbStartKeySerializer implements TokenSerializer<Map<String, AttributeValue>> {
    private static final ObjectMapper OBJECT_MAPPER;
    private static final TypeReference<Map<String, AttributeValue>> DYNAMODB_JACKSON_TYPE = new TypeReference<Map<String, AttributeValue>>() {};

    static {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(AttributeValue.class, new AttributeValueDeserializer());
        module.addSerializer(AttributeValue.class, new AttributeValueSerializer());
        module.addDeserializer(SdkBytes.class, new SdkBytesDeserializer());
        module.addSerializer(SdkBytes.class, new SdkBytesSerializer());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(module);
        OBJECT_MAPPER = objectMapper;
    }

    @Override
    public Map<String, AttributeValue> deserialize(final String token)
            throws InvalidTokenException {
        if (StringUtils.isBlank(token)) {
            throw new InvalidTokenException("The token is blank.");
        }

        try {
            return OBJECT_MAPPER.readValue(token, DYNAMODB_JACKSON_TYPE);
        } catch (IOException e) {
            throw new InvalidTokenException(String.format(
                    "Failed to deserialize token %s from Json.", token), e);
        }
    }

    @Override
    @SneakyThrows(JsonProcessingException.class)
    public String serialize(final Map<String, AttributeValue> startKey) {
        return OBJECT_MAPPER.writeValueAsString(startKey);
    }
}