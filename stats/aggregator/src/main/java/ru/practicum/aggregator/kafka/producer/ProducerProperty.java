package ru.practicum.aggregator.kafka.producer;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "aggregator.kafka.producer.properties")
@ToString
public class ProducerProperty {
    private String bootstrapServers;
    private String keySerializerClass;
    private String valueSerializerClass;
}