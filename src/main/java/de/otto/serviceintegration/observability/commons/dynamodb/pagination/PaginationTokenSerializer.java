package de.otto.serviceintegration.observability.commons.dynamodb.pagination;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.kms.KmsClient;

import java.time.Duration;
import java.util.Map;

/**
 * Implementation of {@link TokenSerializer} to serialize/deserialize
 * pagination token for List APIs.
 *
 * <p>It chians {@link DynamoDbStartKeySerializer}, {@link TimeBasedTokenSerializer}
 * and {@link EncryptedTokenSerializer} to serialize/deserialize a DynamoDb start key
 * to a String token that is URL friendly.
 *
 * <p>Serialize flow:
 * DynamoDb start key -> Json string -> Json String with TTL -> base64 encoded cipher text (Token)
 *
 * <p>Deserialize flow:
 * Token -> Base64 decoded plaintext -> Json String with TTL -> Json String -> DynamoDb start key
 */
public class PaginationTokenSerializer implements TokenSerializer<Map<String, AttributeValue>> {
    private static final Long DEFAULT_TOKEN_TTL_IN_MINUTES = 5L;
    private final TokenSerializer<Map<String, AttributeValue>> dynamoDbStartKeySerializer;
    private final TokenSerializer<String> timeBasedTokenSerializer;
    private final TokenSerializer<String> encryptedTokenSerializer;

    /**
     * Construct PaginationTokenSerializer from KmsClient, KmsKeyId and default TTL (5 Minutes)
     */
    public PaginationTokenSerializer(final KmsClient kms, final String kmsKeyId) {
        this(kms, kmsKeyId, Duration.ofMinutes(DEFAULT_TOKEN_TTL_IN_MINUTES));
    }

    /**
     * Construct PaginationTokenSerializer from KmsClient, KmsKeyId and TTL
     */
    public PaginationTokenSerializer(final KmsClient kms, final String kmsKeyId, final Duration tokenTTL) {
        this.dynamoDbStartKeySerializer = new DynamoDbStartKeySerializer();
        this.timeBasedTokenSerializer = new TimeBasedTokenSerializer(tokenTTL);
        this.encryptedTokenSerializer = new EncryptedTokenSerializer(kms, kmsKeyId);
    }

    @Override
    public Map<String, AttributeValue> deserialize(final String token)
            throws InvalidTokenException {
        String plaintext = encryptedTokenSerializer.deserialize(token);
        String json = timeBasedTokenSerializer.deserialize(plaintext);
        return dynamoDbStartKeySerializer.deserialize(json);
    }

    @Override
    public String serialize(final Map<String, AttributeValue> startKey) {
        String json = dynamoDbStartKeySerializer.serialize(startKey);
        String jsonWithTtl = timeBasedTokenSerializer.serialize(json);
        return encryptedTokenSerializer.serialize(jsonWithTtl);
    }
}
