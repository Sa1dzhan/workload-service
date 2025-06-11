package com.app.workloadservice.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TransactionIdFilter extends OncePerRequestFilter {

    private static final String TRANSACTION_ID_KEY = "transactionId";

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            final String transactionId = request.getHeader(TRANSACTION_ID_KEY);
            MDC.put(TRANSACTION_ID_KEY, transactionId);
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(TRANSACTION_ID_KEY);
        }
    }
} 
