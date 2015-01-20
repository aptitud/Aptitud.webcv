package se.webcv.filter;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Filter;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class SimpleHttpsRestrictFilterTest {

    final FilterConfig filterConfig = mock(FilterConfig.class);
    final SimpleHttpsRestrictFilter filter = new SimpleHttpsRestrictFilter();
    final HttpServletRequest request = mock(HttpServletRequest.class);
    final HttpServletResponse response = mock(HttpServletResponse.class);
    final FilterChain chain = mock(FilterChain.class);

    @Before
    public void setUp() throws Exception {
        filter.init(filterConfig);
    }

    @Test
    public void httpsShouldNotBeRedirected() throws Exception {
        when(request.isSecure()).thenReturn(true);
        filter.doFilter(request, response, chain);
        verify(chain).doFilter(request, response);
    }

    @Test
    public void forwardedHttpsShouldNotBeRedirected() throws Exception {
        when(request.isSecure()).thenReturn(false);
        when(request.getHeader("X-Forwarded-Proto")).thenReturn("https");
        filter.doFilter(request, response, chain);
        verify(chain).doFilter(request, response);
    }

    @Test
    public void forwardedHttpShouldBeRedirected() throws Exception {
        when(request.getHeader("X-Forwarded-Proto")).thenReturn("http");
        when(request.getScheme()).thenReturn("http");
        when(request.getServerPort()).thenReturn(80);
        when(request.getServerName()).thenReturn("server");
        when(request.getRequestURI()).thenReturn("/test/dummy");
        filter.doFilter(request, response, chain);
        verify(response).sendRedirect("https://server/test/dummy");
        verify(chain, times(0)).doFilter(request, response);
    }

    @Test
    public void httpShouldBeRedirected() throws Exception {
        when(request.isSecure()).thenReturn(false);
        when(request.getScheme()).thenReturn("http");
        when(request.getServerPort()).thenReturn(80);
        when(request.getServerName()).thenReturn("server");
        when(request.getRequestURI()).thenReturn("/test/dummy");
        filter.doFilter(request, response, chain);
        verify(response).sendRedirect("https://server/test/dummy");
        verify(chain, times(0)).doFilter(request, response);
    }
}