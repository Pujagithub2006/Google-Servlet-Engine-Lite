package org.gse_lite.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimiterFilter implements Filter {
    private static final int MAX_REQUESTS = 5;
    private static final long TIME_WINDOW_MS = 10000; // 10 seconds

    private final Map<String, Deque<Long>> requestHistory = new ConcurrentHashMap<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String clientIp = request.getRemoteAddr();
        long currentTime = System.currentTimeMillis();

        // Retrieve this client's request history
        Deque<Long> timestampsForEachClient =
                requestHistory.computeIfAbsent(
                        clientIp,
                        key -> new ArrayDeque<>()
                );

        // Discard requests that are no longer within the current time window
        while(!timestampsForEachClient.isEmpty() && currentTime-timestampsForEachClient.peekFirst() > TIME_WINDOW_MS) {
            timestampsForEachClient.removeFirst();
        }

        // Reject the request if the client exceeds the allowed request limit.
        if(timestampsForEachClient.size() >= MAX_REQUESTS) {
            response.sendError(
//                    HttpServletResponse.SC_BAD_REQUEST,
                    429,
                    "Too Many Requests"
            );

            return;
        }

        timestampsForEachClient.addLast(currentTime);

        // Continue processing the request
        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
