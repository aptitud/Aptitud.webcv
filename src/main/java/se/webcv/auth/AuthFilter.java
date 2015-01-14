package se.webcv.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import se.webcv.model.AuthResult;
import se.webcv.rest.AuthController;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 *
 */
public class AuthFilter implements Filter {
    final static Logger LOGGER = LoggerFactory.getLogger(AuthFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
                filterConfig.getServletContext());
    }

    @Autowired
    TokenVerifier tokenVerifier;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String requestURI = httpServletRequest.getRequestURI();
        if (!(requestURI.startsWith("/rest/auth")
                || requestURI.startsWith("/rest/bootstrap")
                || requestURI.startsWith("/rest/document"))) {
            String authorization = httpServletRequest.getHeader("Authorization");
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            if (authorization == null || !authorization.startsWith("bearer ")) {
                httpServletResponse.setStatus(401);
                return;
            }
            String token = authorization.substring("bearer ".length());
            try {
                if (tokenVerifier.verify(token) == null) {
                    httpServletResponse.setStatus(401);
                }
            } catch (RuntimeException e) {
                // not a valid token, return 401 for now, we should also handle the case of
                // token expired in a better way
                LOGGER.warn("tokenVerification failed {}", e.getMessage());
                httpServletResponse.setStatus(401);
                return;
            }
        }
        chain.doFilter(request, response);

    }

    @Override
    public void destroy() {

    }
}
