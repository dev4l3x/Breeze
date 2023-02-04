package dev.asiglesias.infrastructure.spring.filters;

import dev.asiglesias.infrastructure.service.auth.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@RequiredArgsConstructor
public class JwtTokenAuthenticationFilter implements Filter {

    private final RequestMatcher matcher = new AntPathRequestMatcher("/sign*");

    private final TokenService service;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String token = request.getHeader("authorization").replace("Bearer ", "");

        if (matcher.matches(request)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        if (Objects.nonNull(token) && !token.isBlank() && service.isValid(token)) {
            String username = service.getUser(token);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, null);
            SecurityContextHolder.createEmptyContext();
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
