//package com.cts.transaction_payment.config;
//
//import org.jspecify.annotations.NonNull;
//import org.springframework.http.HttpRequest;
//import org.springframework.http.client.ClientHttpRequestExecution;
//import org.springframework.http.client.ClientHttpRequestInterceptor;
//import org.springframework.http.client.ClientHttpResponse;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//import java.io.IOException;
//
//public class JwtInterceptor implements ClientHttpRequestInterceptor {
//    @Override
//    public ClientHttpResponse intercept(@NonNull HttpRequest request, byte @NonNull [] body, @NonNull ClientHttpRequestExecution execution) throws IOException {
//        // Get the token from the current request's security context
//        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        if (attributes != null) {
//            String token = attributes.getRequest().getHeader("Authorization");
//            if (token != null) {
//                request.getHeaders().add("Authorization", token);
//            }
//        }
//        return execution.execute(request, body);
//    }
//}
