package se.webcv.auth;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Created by marcus on 10/01/15.
 */
@Component
public class GoogleTokenVerifier implements TokenVerifier {
    @Autowired
    private RestTemplate restTemplate;

    public UserAndDomain verify(String token) {
        JsonNode peopleData = restTemplate.getForObject("https://www.googleapis.com/plus/v1/people/me?access_token={token}", JsonNode.class, token);
        if (peopleData == null) {
            throw new NotAuthenticatedException("No data returned from verification provider");
        }
        if (peopleData.get("domain") == null) {
            throw new NotAuthenticatedException("No domain provided");
        }
        return new UserAndDomain(token, peopleData.get("domain").asText(), peopleData.get("displayName") != null ? peopleData.get("displayName").asText() : null);
    }
}
