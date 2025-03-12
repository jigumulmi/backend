package com.jigumulmi.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.jigumulmi.place.dto.TimeDto;
import java.io.IOException;
import java.time.LocalTime;
import org.springframework.boot.jackson.JsonComponent;

@JsonComponent
public class LocalTimeJsonCustomizer {

    public static class Serializer extends JsonSerializer<LocalTime> {
        @Override
        public void serialize(LocalTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (value == null) {
                gen.writeNull();
            } else {
                gen.writeObject(TimeDto.from(value));
            }
        }
    }

    public static class Deserializer extends JsonDeserializer<LocalTime> {
        @Override
        public LocalTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            TimeDto timeDto = p.readValueAs(TimeDto.class);
            return TimeDto.toLocalTime(timeDto);
        }
    }
}
