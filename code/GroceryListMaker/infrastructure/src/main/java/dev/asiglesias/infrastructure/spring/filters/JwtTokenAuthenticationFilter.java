package dev.asiglesias.infrastructure.spring.filters;

import dev.asiglesias.infrastructure.auth.services.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class JwtTokenAuthenticationFilter implements Filter {

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

        Optional<String> token = Optional.ofNullable(request.getHeader("authorization"))
                .map(t -> t.replace("Bearer ", ""));

        if (token.isPresent() && !token.get().isBlank() && service.isValid(token.get())) {
            String username = service.getUser(token.get());
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, null,
                    List.of(new SimpleGrantedAuthority("USER")));
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
