package se.webcv.auth;

/**
 * Created by marcus on 10/01/15.
 */
public interface TokenVerifier {
    public UserAndDomain verify(String token);

    public static class UserAndDomain {
        public final String token;
        public final String domain;
        public final String displayName;

        public UserAndDomain(String token, String domain, String displayName) {
            this.token = token;
            this.domain = domain;
            this.displayName = displayName;
        }
    }
}
