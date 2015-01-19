package se.webcv.rest;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.google.gson.Gson;

import se.webcv.auth.LogoutHandler;
import se.webcv.auth.NotAuthenticatedException;
import se.webcv.auth.TokenVerifier;
import se.webcv.model.AuthRequest;
import se.webcv.model.AuthResult;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private static final String APTITUD_SE = "aptitud.se";
    public static final String SESSION_KEY = "webcv.auth";

    @Autowired
    TokenVerifier tokenVerifier;

    @Autowired
    LogoutHandler logoutHandler;

    @RequestMapping(produces = "application/json", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public AuthResult auth(@RequestBody(required = true) JsonNode body) {
        if (body.get("accessToken") == null || body.get("accessToken").asText() == null) {
            throw new NotAuthenticatedException("No token");
        }
        TokenVerifier.UserAndDomain userAndDomain = tokenVerifier.verify(body.get("accessToken").asText());
        if (!userAndDomain.domain.equalsIgnoreCase(APTITUD_SE)) {
            throw new NotAuthenticatedException("Wrong domain");
        }
        return new AuthResult(userAndDomain.token, userAndDomain.domain, userAndDomain.displayName, true);
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void logout(@RequestBody(required = true) JsonNode body) {
        if (body.get("accessToken") != null && body.get("accessToken").asText() != null) {
            logoutHandler.logout(body.get("accessToken").asText());
        }
    }
}
