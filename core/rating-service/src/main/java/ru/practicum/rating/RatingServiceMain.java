package ru.practicum.rating;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.practicum.interactionapi.event.event.client.AdminEventClient;
import ru.practicum.interactionapi.user.client.UserClient;

@EnableFeignClients(clients = {UserClient.class, AdminEventClient.class})
@SpringBootApplication
public class RatingServiceMain {
    public static void main(String[] args) {
        SpringApplication.run(RatingServiceMain.class, args);
    }
}