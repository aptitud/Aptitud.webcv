package se.webcv.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpStatusCodeException;

import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class GlobalControllerExceptionHandler {
    static final Logger logger = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ErrorResponse handleException(Exception ex, HttpServletResponse response) throws Exception {
        ResponseStatus responseStatus = AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class);
        if (responseStatus != null) {
            response.setStatus(responseStatus.value().value());
            return new ErrorResponse(responseStatus.reason(), ex.getMessage());
        }
        logger.error("Unexpected exception", ex);
        if (ex instanceof HttpStatusCodeException) {
            response.setStatus(((HttpStatusCodeException) ex).getStatusCode().value());
            return new ErrorResponse(ex.getMessage());
        }
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ErrorResponse(ex.getMessage());
    }

    public static class ErrorResponse {
        public final String reason;
        public final String message;

        public ErrorResponse(String message) {
            this.reason = null;
            this.message = message;
        }

        public ErrorResponse(String reason, String message) {
            this.reason = reason;
            this.message = message;
        }
    }
}
