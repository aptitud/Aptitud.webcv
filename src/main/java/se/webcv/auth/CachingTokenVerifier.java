package se.webcv.auth;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutionException;

/**
 * Created by marcus on 10/01/15.
 */
@Component
@Primary
public class CachingTokenVerifier implements TokenVerifier, LogoutHandler {
    @Autowired
    private GoogleTokenVerifier tokenVerifier;

    private final LoadingCache<String, UserAndDomain> cachedVerifier = CacheBuilder.from(System.getProperty("caching.token.verifier.spec", "maximumSize=1000,expireAfterWrite=5m"))
            .build(new CacheLoader<String, UserAndDomain>() {
                @Override
                public UserAndDomain load(String key) throws Exception {
                    return tokenVerifier.verify(key);
                }
            });

    public UserAndDomain verify(String token) {
        try {
            return cachedVerifier.get(token);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof RuntimeException) {
                throw (RuntimeException) e.getCause();
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public void logout(String token) {
        cachedVerifier.invalidate(token);
    }
}
