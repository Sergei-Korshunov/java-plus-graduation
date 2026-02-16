package ru.practicum.interactionapi.exception.feign;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;
import ru.practicum.interactionapi.exception.ConflictException;
import ru.practicum.interactionapi.exception.NotFoundException;

@Component
public class CustomErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder errorDecoder = new Default();

    protected final int CONFLICT = 409;
    protected final int NOT_FOUND = 404;

    @Override
    public Exception decode(String s, Response response) {
        if (response.status() == CONFLICT) {
            throw new ConflictException("Conflict");
        } else if (response.status() == NOT_FOUND) {
            throw new NotFoundException("Not Found");
        } else {
            return errorDecoder.decode(s, response);
        }
    }
}