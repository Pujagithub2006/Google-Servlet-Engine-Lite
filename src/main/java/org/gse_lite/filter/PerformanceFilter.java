package org.gse_lite.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class PerformanceFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        long startTime = System.nanoTime();
        filterChain.doFilter(servletRequest, servletResponse);
        long endTime = System.nanoTime();
        long executionTime = endTime - startTime;

        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        if(!httpResponse.isCommitted()) {
            httpResponse.setHeader(
                    "X-Response-Time-Ns",
                    String.valueOf(executionTime)
            );
        }
    }

    @Override
    public void destroy() {
    }
}