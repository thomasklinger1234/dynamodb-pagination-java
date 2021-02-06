package de.otto.serviceintegration.observability.commons.dynamodb.pagination.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.SdkBytes;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Jackson Json Serializer for {@link SdkBytes}.
 */
@RequiredArgsConstructor
public class SdkBytesSerializer extends JsonSerializer<SdkBytes> {
    private final Charset charset;

    /**
     * Construct SdkBytesSerializer using default charset.
     */
    public SdkBytesSerializer() {
        this(Charset.defaultCharset());
    }

    @Override
    public void serialize(SdkBytes sdkBytes, JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(sdkBytes.asString(charset));
    }
}