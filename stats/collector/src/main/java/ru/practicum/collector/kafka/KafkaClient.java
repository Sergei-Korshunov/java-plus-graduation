package ru.practicum.collector.kafka;

import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Slf4j
@EnableConfigurationProperties({KafkaProperty.class})
@Component
public class KafkaClient {
    @Getter
    private final KafkaProperty kafkaProperty;
    protected final Producer<Long, SpecificRecordBase> producer;

    @Autowired
    public KafkaClient(KafkaProperty kafkaProperty) {
        log.info("Создание Kafka-клиента");
        this.kafkaProperty = kafkaProperty;
        producer = producerInstance();
        log.info("Kafka-клиент успешно создан");
    }

    protected Producer<Long, SpecificRecordBase> producerInstance() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperty.getBootstrapServers());
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaProperty.getKeySerializerClass());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafkaProperty.getValueSerializerClass());

        return new KafkaProducer<>(properties);
    }

    public void sendData(String topicName, Integer partition, Long timestamp, Long key, SpecificRecordBase value) {
        ProducerRecord<Long, SpecificRecordBase> producerRecord = new ProducerRecord<>(
                topicName,
                partition,
                timestamp,
                key,
                value
        );

        log.info("Отправка данных в топик {}", topicName);
        producer.send(producerRecord);
        producer.flush();
        log.info("Данные успешно отправлены в топик");
    }

    @PreDestroy
    public void close() {
        producer.flush();
        producer.close();
        log.info("Закрытие Producer");
    }
}