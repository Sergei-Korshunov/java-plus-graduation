package ru.practicum.aggregator.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.util.Properties;

@EnableConfigurationProperties({ConsumerProperty.class})
@Configuration
public class Consumer {
    private final ConsumerProperty consumerProperty;

    @Autowired
    public Consumer(ConsumerProperty consumerProperty) {
        this.consumerProperty = consumerProperty;
    }

    @Bean
    public KafkaConsumer<Long, UserActionAvro> getConsumerInstance() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, consumerProperty.getBootstrapServers());
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, consumerProperty.getKeyDeserializerClass());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, consumerProperty.getValueDeserializerClass());

        properties.put(ConsumerConfig.GROUP_ID_CONFIG, consumerProperty.getGroupId());
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, consumerProperty.getClientId());
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, consumerProperty.getEnableAutoCommit());

        return new KafkaConsumer<>(properties);
    }
}