package ru.job4j.site.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import ru.job4j.site.exception.IdNotFoundException;

import java.io.IOException;

/**
 * @author Svistunov Mikhail
 * @version 1.0
 */
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {
    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return (response.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR
                || response.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR);
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        if (response.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR) {
            throw new IdNotFoundException("ID не найден");
        }
        if (response.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR
                && response.getStatusCode() == HttpStatus.NOT_FOUND) {
            throw new IdNotFoundException("Пользователь не найден");
        }
    }
}
