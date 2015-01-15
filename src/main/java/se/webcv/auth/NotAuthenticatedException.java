package se.webcv.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by marcus on 10/01/15.
 */
@ResponseStatus(value= HttpStatus.UNAUTHORIZED, reason="Not authenticated")
public class NotAuthenticatedException extends RuntimeException {
    public NotAuthenticatedException(String message) {
        super(message);
    }
}
