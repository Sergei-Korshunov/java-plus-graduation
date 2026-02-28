package ru.practicum.collector.kafka;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "collector.kafka.producer.properties")
@ToString
public class KafkaProperty {
    private String bootstrapServers;
    private String keySerializerClass;
    private String valueSerializerClass;
    @Value("${collector.kafka.producer.topic.user-action}")
    private String topicUserAction;
}