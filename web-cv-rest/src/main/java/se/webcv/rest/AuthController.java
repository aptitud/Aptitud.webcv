package se.webcv.rest;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.google.gson.Gson;

import se.webcv.model.AuthRequest;
import se.webcv.model.AuthResult;

@Controller
@RequestMapping("/auth")
public class AuthController {

	private static final String APTITUD_SE = "aptitud.se";

	@RequestMapping(produces = "application/json", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public AuthResult auth(@RequestParam(required = true) String userInfo) {
		AuthResult result = new AuthResult();
		Gson gson = new Gson();
		AuthRequest authrequest = gson.fromJson(userInfo, AuthRequest.class);
		result.setAuthenticated(APTITUD_SE.equals(authrequest.getDomain()));
        return result;
    }
}
