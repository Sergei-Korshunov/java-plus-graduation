package ru.practicum.analyzer.kafka.consumer;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "analyzer.kafka.consumer.events-similarity.properties")
public class ConsumerEventsSimilarityProperty {
    private String bootstrapServers;
    private String keyDeserializerClass;
    private String valueDeserializerClass;
    private String groupId;
    private String clientId;
    private String enableAutoCommit;
    private String pollTimeout;
}