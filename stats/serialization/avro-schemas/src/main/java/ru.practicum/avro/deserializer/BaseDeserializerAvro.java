package ru.practicum.avro.deserializer;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.serialization.Deserializer;
import ru.practicum.avro.deserializer.exeption.DeserializationException;
import ru.practicum.util.Json;

@Slf4j
public class BaseDeserializerAvro<T extends SpecificRecordBase> implements Deserializer<T> {

    private final DecoderFactory decoderFactory;
    private final DatumReader<T> reader;

    public BaseDeserializerAvro(Schema schema) {
        this(DecoderFactory.get(), schema);
    }

    public BaseDeserializerAvro(DecoderFactory decoderFactory, Schema schema) {
        this.decoderFactory = decoderFactory;
        this.reader = new SpecificDatumReader<>(schema);
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        try {
            if (data != null) {
                BinaryDecoder decoder = decoderFactory.binaryDecoder(data, null);
                T object = reader.read(null, decoder);

                log.info("Объект Avro в JSON формате {}", Json.avroObjectToJson(object));
                return object;
            }

            log.info("В топике {} двоичные данные не обнаружены", topic);
            return null;
        } catch (Exception exception) {
            log.error("Ошибка десериализации данных для топика: {}, ошибка: ", topic, exception);
            throw new DeserializationException(String.format("Ошибка десериализации данных из топика %s", topic), exception);
        }
    }
}