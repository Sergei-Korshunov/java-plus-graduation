package ru.practicum.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecord;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
public class Json {
    private final static ObjectMapper objectMapper = new ObjectMapper();

    static  {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    private Json() {}

    public static String simpleObjectToJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Ошибка сериализации простого объекта в json формат: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static <T extends SpecificRecord> String avroObjectToJson(T avroObject) {
        DatumWriter<T> writer = new SpecificDatumWriter<>(avroObject.getSchema());
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Encoder jsonEncoder = EncoderFactory.get().jsonEncoder(avroObject.getSchema(), baos, true);
            writer.write(avroObject, jsonEncoder);
            jsonEncoder.flush();

            return baos.toString();
        } catch (IOException e) {
            log.error("Ошибка сериализации объекта Avro в json формат: {}", e.getMessage());
        }
        return "";
    }

    public static <T extends GeneratedMessageV3> String protoObjectToJson(T protoObject) {
        JsonFormat.Printer printer = JsonFormat.printer().omittingInsignificantWhitespace();
        try {
            return printer.print(protoObject);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
    }
}
