package se.webcv.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component("tokenVerifier")
@Profile("unit-test")
public class UnitTokenVerifier implements TokenVerifier {

    private UserAndDomain userAndDomain;

    public void authorized() {
        userAndDomain = new UserAndDomain("token", "aptitud.se", "Unit test");
    }

    public void notAuthorized() {
        userAndDomain = null;
    }

    public UserAndDomain verify(String token) {
        if (userAndDomain == null) {
            throw new NotAuthenticatedException("No data returned from verification provider");
        }
        return userAndDomain;
    }
}
