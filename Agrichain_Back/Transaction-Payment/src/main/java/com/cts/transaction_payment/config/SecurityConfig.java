//package com.cts.transaction_payment.config;
//
//import com.cts.transaction_payment.filter.JwtFilter;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity // Enables @PreAuthorize support used in your controllers
//public class SecurityConfig {
//
//    @Autowired
//    private JwtFilter jwtFilter;
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable()) // Disabled for REST APIs
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // JWT is stateless
//                .authorizeHttpRequests(auth -> auth
//                        // --- 1. Public Endpoints ---
////                        .requestMatchers("/users/login", "/users/register").permitAll()
////                        .requestMatchers("/market/test-proxy").permitAll()
////
////                        // --- 2. Identity & Farmer Management ---
////                        .requestMatchers(HttpMethod.POST, "/farmers").hasRole("FARMER")
////                        .requestMatchers("/farmers/**").hasAnyRole("ADMIN", "OFFICER")
////                        .requestMatchers("/documents/**").hasAnyRole("FARMER", "OFFICER", "ADMIN")
////
////                        // --- 3. Market & Crop Listings ---
////                        .requestMatchers(HttpMethod.POST, "/market/createlisting").hasRole("FARMER")
////                        .requestMatchers(HttpMethod.PUT, "/market/listings/validate/*").hasRole("OFFICER")
////                        .requestMatchers(HttpMethod.POST, "/market/placeorder").hasRole("TRADER")
////                        .requestMatchers("/market/orders/**").hasAnyRole("TRADER", "OFFICER", "ADMIN")
////                        .requestMatchers("/market/listings/**").hasAnyRole("FARMER", "TRADER", "OFFICER", "ADMIN")
////
////                        // --- 4. Subsidies & Disbursements ---
////                        .requestMatchers(HttpMethod.POST, "/subsidy-programs/**").hasRole("ADMIN")
////                        .requestMatchers(HttpMethod.GET, "/subsidy-programs/**").authenticated()
////                        .requestMatchers(HttpMethod.POST, "/disbursements").hasRole("FARMER")
////                        .requestMatchers(HttpMethod.PATCH, "/disbursements/*/review").hasAnyRole("OFFICER", "ADMIN")
//
////                        // --- 5. Transactions & Payments ---
////                        .requestMatchers("/api/transactions/**").hasAnyRole("TRADER", "FARMER", "ADMIN")
////                        .requestMatchers("/api/payments/**").hasAnyRole("TRADER", "ADMIN")
//////
//                                // --- 5. Transactions & Payments ---
//                                .requestMatchers("/transactions/**").permitAll()
//                                .requestMatchers("/payments/**").permitAll()
////
////                        // --- 6. Compliance & Audits ---
////                        .requestMatchers("/api/audits/**").hasAnyRole("AUDITOR", "ADMIN")
////                        .requestMatchers("/api/compliances/**").hasAnyRole("COMPLIANCE", "ADMIN")
////                        .requestMatchers("/audit-logs/**").hasRole("ADMIN")
////
////                        // --- 7. Reports & Notifications ---
////                        .requestMatchers("/api/reports/**").hasRole("ADMIN")
////                        .requestMatchers("/api/notifications/**").authenticated()
////                        .requestMatchers("/actuator/**").permitAll() // Add this line
////
////                        // --- 8. Final Catch-all ---
//                        .anyRequest().authenticated()
//                )
//                // Add JWT Filter before the standard UsernamePassword filter
//                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
//                .httpBasic(Customizer.withDefaults()); // Or your specific login config
//
//
//        return http.build();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
//        return config.getAuthenticationManager();
//    }
//}
