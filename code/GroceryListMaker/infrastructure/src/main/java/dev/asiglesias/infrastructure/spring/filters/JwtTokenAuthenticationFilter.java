package dev.asiglesias.infrastructure.spring.filters;

import dev.asiglesias.infrastructure.auth.services.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
public class JwtTokenAuthenticationFilter implements Filter {

    public static final String ACCESS_TOKEN = "access_token";
    private final RequestMatcher matcher = new AntPathRequestMatcher("/sign*");

    private final JwtTokenService service;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (matcher.matches(request)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        Optional<String> token = getTokenFromHeaders(request);

        if (token.isEmpty()) {
            token = getTokenFromCookies(request);
        }

        if (token.isPresent() && !token.get().isBlank() && service.isValid(token.get())) {
            String username = service.getUser(token.get());
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, null,
                    List.of(new SimpleGrantedAuthority("USER")));
            SecurityContextHolder.createEmptyContext();
            SecurityContextHolder.getContext().setAuthentication(auth);
        } else {
           response.sendRedirect("/signin");
           return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private Optional<String> getTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (Objects.isNull(cookies)) {
            return Optional.empty();
        }

        Optional<Cookie> accessCookie = Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(ACCESS_TOKEN))
                .findAny();
        return accessCookie.map(Cookie::getValue);
    }

    private Optional<String> getTokenFromHeaders(HttpServletRequest request) {
        Optional<String> token = Optional.ofNullable(request.getHeader("authorization"))
                .map(t -> t.replace("Bearer ", ""));
        return token;
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
