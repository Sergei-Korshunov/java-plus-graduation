package ru.practicum.interactionapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.practicum.interactionapi.exception.ErrorHandler;

@Configuration
@Import({ErrorHandler.class, JacksonConfig.class, FeignConfig.class})
public class AppConfig {

}