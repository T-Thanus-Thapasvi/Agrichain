//package com.cts.croplisting_service.filter;
//
//import com.cts.croplisting_service.service.JwtService;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Component
//public class JwtFilter extends OncePerRequestFilter {
//
//    @Autowired
//    private JwtService jwtService;
//
//    @Autowired
//    private UserDetailsService userDetailsService;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//
//        final String authHeader = request.getHeader("Authorization");
//        final String jwt;
//        final String userName;
//
//        // 1. Check for Bearer Token
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        jwt = authHeader.substring(7);
//        userName = jwtService.extractUserName(jwt);
//
//        // 2. If valid and not already authenticated in this Security Context
//        if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userName);
//
//            if (jwtService.validateToken(jwt, userDetails)) {
//
//                // 3. Extract Roles from JWT and add "ROLE_" prefix
//                // This step is critical to match .hasRole("OFFICER") in SecurityConfig
//                List<String> roles = jwtService.extractRoles(jwt); // Ensure your JwtService has this method
//
//                List<GrantedAuthority> authorities = roles.stream()
//                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
//                        .collect(Collectors.toList());
//
//                // 4. Create Authentication Token with the NEW authorities
//                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
//                        userDetails,
//                        null,
//                        authorities
//                );
//
//                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//
//                // 5. Set the Context
//                SecurityContextHolder.getContext().setAuthentication(authToken);
//
//                // DEBUG: Verify exactly what Spring sees in the console
//                System.out.println("Authenticated User: " + userName + " | Authorities: " + authorities);
//            }
//        }
//
//        filterChain.doFilter(request, response);
//    }
//}