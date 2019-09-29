package se.webcv.auth;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Created by marcus on 10/01/15.
 */
@Component("tokenVerifier")
@Profile("!unit-test")
public class GoogleTokenVerifier implements TokenVerifier {
    @Autowired
    private RestTemplate restTemplate;

    public UserAndDomain verify(String token) {
        JsonNode peopleData = restTemplate.getForObject("https://oauth2.googleapis.com/tokeninfo?id_token={token}", JsonNode.class, token);
        if (peopleData == null) {
            throw new NotAuthenticatedException("No data returned from verification provider");
        }
        if (peopleData.get("hd") == null) {
            throw new NotAuthenticatedException("No domain provided");
        }
        return new UserAndDomain(token, peopleData.get("hd").asText(), peopleData.get("name") != null ? peopleData.get("name").asText() : null);
    }
}
