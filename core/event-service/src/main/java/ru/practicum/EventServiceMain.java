package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import ru.korshunov.statsclient.StatsClient;
import ru.practicum.interactionapi.config.AppConfig;
import ru.practicum.interactionapi.request.client.PrivateRequestClient;
import ru.practicum.interactionapi.user.client.UserClient;

@SpringBootApplication
@EnableFeignClients(clients = {UserClient.class, PrivateRequestClient.class, StatsClient.class})
@Import({AppConfig.class})
public class EventServiceMain {
    public static void main(String[] args) {
        SpringApplication.run(EventServiceMain.class, args);
    }
}