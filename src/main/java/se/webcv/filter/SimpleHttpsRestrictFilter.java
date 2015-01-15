package se.webcv.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpUtils;
import java.io.IOException;

public class SimpleHttpsRestrictFilter implements Filter {

    private String redirectUrl;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;
        if (!isSecure(request) && !isLocalHost(request)) {
            String redirectTo = redirectUrl;
            if (redirectTo == null) {
                redirectTo = HttpUtils.getRequestURL(request).toString().replace("http", "https");
            }
            response.sendRedirect(redirectTo);
            return;
        }
        chain.doFilter(req, res);
    }

    private boolean isSecure(HttpServletRequest request) {
        if (request.isSecure()) {
            return true;
        }
        String proto = request.getHeader("X-Forwarded-Proto");
        if (proto != null && proto.equalsIgnoreCase("https")) {
            return true;
        }
        return false;
    }

    private boolean isLocalHost(HttpServletRequest request) {
        String serverName = request.getServerName();
        return serverName.equalsIgnoreCase("localhost")
                || serverName.equals("127.0.0.1");
    }

    public void destroy() {
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
        redirectUrl = config.getInitParameter("redirect-url");
        if (redirectUrl == null) {
            redirectUrl = System.getProperty("redirect-url");
        }
    }
}
