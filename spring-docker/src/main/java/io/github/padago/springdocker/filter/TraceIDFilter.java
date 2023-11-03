package io.github.padago.springdocker.filter;

import io.github.padago.springdocker.util.SnowflakeIDGenerator;
import org.slf4j.MDC;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author fdrama
 * date 2023年11月03日 15:15
 */
@WebFilter(urlPatterns = "/*", filterName = "traceIDFilter")
public class TraceIDFilter extends GenericFilterBean {

    private static final String TRACE_ID = "TRACE_ID";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String traceId = ((HttpServletRequest) request).getHeader(TRACE_ID);

        if (traceId == null || traceId.isEmpty()) {
            traceId = String.valueOf(SnowflakeIDGenerator.getInstance(1L).generateID());
        }
        MDC.put(TRACE_ID, traceId);
        chain.doFilter(request, response);
    }
}
