package de.otto.serviceintegration.observability.commons.dynamodb.pagination.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.SdkBytes;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Jackson Json Deserializer for {@link SdkBytes}.
 */
@RequiredArgsConstructor
public class SdkBytesDeserializer extends JsonDeserializer<SdkBytes> {
    private final Charset charset;

    /**
     * Construct SdkBytesDeserializer using default charset.
     */
    public SdkBytesDeserializer() {
        this(Charset.defaultCharset());
    }

    @Override
    public SdkBytes deserialize(JsonParser jsonParser,
                                DeserializationContext deserializationContext)
            throws IOException, JsonProcessingException {
        return SdkBytes.fromString(jsonParser.getValueAsString(), charset);
    }
}
