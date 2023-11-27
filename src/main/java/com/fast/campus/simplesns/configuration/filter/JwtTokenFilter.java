package com.fast.campus.simplesns.configuration.filter;

import com.fast.campus.simplesns.util.JwtTokenUtils;
import com.fast.campus.simplesns.model.User;
import com.fast.campus.simplesns.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final UserService userService;

    private final String secretKey;

    private final static List<String> TOKEN_IN_PARAM_URLS = List.of("/api/v1/users/alarm/subscribe");

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String token;

        if (TOKEN_IN_PARAM_URLS.contains(request.getRequestURI())) {
            log.info("Request with {} check query param", request.getRequestURI());
            token = request.getQueryString().split("=")[1].trim();
        } else {
            String header = request.getHeader(HttpHeaders.AUTHORIZATION);

            if (header == null || !header.startsWith("Bearer ")) {
                log.warn("Authorization Header does not start with Bearer");
                chain.doFilter(request, response);
                return;
            }
            token = header.split(" ")[1].trim();
        }



        User userDetails = userService.loadUserByUsername(JwtTokenUtils.getUsername(token, secretKey));

        if (!JwtTokenUtils.validate(token, userDetails, secretKey)) {
            chain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null,
                userDetails.getAuthorities()
        );

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

}
