package ru.practicum.avro.serializer;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;
import ru.practicum.util.Json;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
public class SerializerAvro implements Serializer<SpecificRecordBase> {

    private final EncoderFactory encoderFactory;
    private BinaryEncoder encoder;

    public SerializerAvro() {
        this(EncoderFactory.get());
    }

    public SerializerAvro(EncoderFactory encoderFactory) {
        this.encoderFactory = encoderFactory;
    }

    @Override
    public byte[] serialize(String topic, SpecificRecordBase data) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            if (data != null) {
                DatumWriter<SpecificRecordBase> writer = new SpecificDatumWriter<>(data.getSchema());
                encoder = encoderFactory.binaryEncoder(out, encoder);
                writer.write(data, encoder);
                encoder.flush();
            }

            log.info("Объект Avro в JSON формате {}", Json.avroObjectToJson(data));
            return out.toByteArray();
        } catch (IOException exception) {
            log.error("Ошибка сериализации данных для топика: {}, ошибка: ", topic, exception);
            throw new SerializationException(String.format("Ошибка десериализации данных из топика %s", topic), exception);
        }
    }
}