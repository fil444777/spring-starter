package spring.http.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public String handleResponseStatusException(ResponseStatusException ex, Model model) {
        log.warn("HTTP {}: {}", ex.getStatusCode(), ex.getReason());

        model.addAttribute("status", ex.getStatusCode().value());
        model.addAttribute("error", getErrorTitle((HttpStatus) ex.getStatusCode()));
        model.addAttribute("message", ex.getReason());
        model.addAttribute("isClientError", true);

        return "error/error500";
    }

    private String getErrorTitle(HttpStatus status) {
        return switch (status) {
            case PAYLOAD_TOO_LARGE -> "Файл слишком большой";
            case NOT_FOUND -> "Страница не найдена";
            case FORBIDDEN -> "Доступ запрещён";
            case BAD_REQUEST -> "Некорректный запрос";
            default -> status.getReasonPhrase();
        };
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception exception){
        log.error("Failed to return response", exception);
        return "error/error500";
    }
}
