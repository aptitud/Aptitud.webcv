package se.webcv.auth;

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
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        if (!httpServletRequest.getRequestURI().startsWith("/auth")) {
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            HttpSession session = httpServletRequest.getSession(false);
            if (session == null || session.getAttribute(AuthController.SESSION_KEY) == null) {
                httpServletResponse.setStatus(401);
                return;
            }
            // check same token in session and in request, instead of calling the verifier for each call
            // use the session as a cache
            AuthResult authResult = (AuthResult) session.getAttribute(AuthController.SESSION_KEY);
            String authorization = httpServletRequest.getHeader("Authorization");
            if (authorization == null || !authorization.startsWith("bearer ")) {
                httpServletResponse.setStatus(401);
                return;
            }
            String token = authorization.substring("bearer ".length());
            if (!authResult.getToken().equals(token)) {
                httpServletResponse.setStatus(401);
                return;
            }
            httpServletRequest.setAttribute(AuthController.SESSION_KEY, authResult);
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
