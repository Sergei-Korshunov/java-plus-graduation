package ru.practicum.aggregator.kafka.consumer;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "aggregator.kafka.consumer.properties")
@ToString
public class ConsumerProperty {
    private String bootstrapServers;
    private String keyDeserializerClass;
    private String valueDeserializerClass;
    private String groupId;
    private String clientId;
    private String enableAutoCommit;
    private String pollTimeout;
}