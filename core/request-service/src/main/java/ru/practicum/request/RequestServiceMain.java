package ru.practicum.request;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import ru.practicum.interactionapi.config.AppConfig;
import ru.practicum.interactionapi.event.event.client.AdminEventClient;
import ru.practicum.interactionapi.user.client.UserClient;

@SpringBootApplication
@EnableFeignClients(clients = {UserClient.class, AdminEventClient.class})
@Import({AppConfig.class})
public class RequestServiceMain {
    public static void main(String[] args) {
        SpringApplication.run(RequestServiceMain.class, args);
    }
}