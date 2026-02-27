package ru.practicum.analyzer.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.util.Properties;

@EnableConfigurationProperties({ConsumerEventsSimilarityProperty.class, ConsumerUserActionProperty.class})
@Configuration
public class Consumer {

    private final ConsumerEventsSimilarityProperty consumerEventsSimilarityProperty;
    private final ConsumerUserActionProperty consumerUserActionProperty;

    @Autowired
    public Consumer(ConsumerEventsSimilarityProperty consumerEventsSimilarityProperty,
                    ConsumerUserActionProperty consumerUserActionProperty) {
        this.consumerEventsSimilarityProperty = consumerEventsSimilarityProperty;
        this.consumerUserActionProperty = consumerUserActionProperty;
    }

    @Bean
    public KafkaConsumer<Long, EventSimilarityAvro> getConsumerEventsSimilarityInstance() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, consumerEventsSimilarityProperty.getBootstrapServers());
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, consumerEventsSimilarityProperty.getKeyDeserializerClass());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, consumerEventsSimilarityProperty.getValueDeserializerClass());

        properties.put(ConsumerConfig.GROUP_ID_CONFIG, consumerEventsSimilarityProperty.getGroupId());
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, consumerEventsSimilarityProperty.getClientId());
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, consumerEventsSimilarityProperty.getEnableAutoCommit());

        return new KafkaConsumer<>(properties);
    }

    @Bean
    public KafkaConsumer<Long, UserActionAvro> getConsumerUserActionInstance() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, consumerUserActionProperty.getBootstrapServers());
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, consumerUserActionProperty.getKeyDeserializerClass());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, consumerUserActionProperty.getValueDeserializerClass());

        properties.put(ConsumerConfig.GROUP_ID_CONFIG, consumerUserActionProperty.getGroupId());
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, consumerUserActionProperty.getClientId());
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, consumerUserActionProperty.getEnableAutoCommit());

        return new KafkaConsumer<>(properties);
    }
}