package dev.asiglesias.infrastructure.auth.controllers;

import dev.asiglesias.application.auth.SignInUserUseCase;
import dev.asiglesias.application.auth.SignInUserUseCaseImpl;
import dev.asiglesias.application.auth.models.UserDataContainer;
import dev.asiglesias.application.auth.repositories.UserRepository;
import dev.asiglesias.application.auth.services.EncodingService;
import dev.asiglesias.application.auth.services.TokenService;
import dev.asiglesias.infrastructure.auth.controllers.dto.web.UserModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class AuthenticationWebController {

    private final SignInUserUseCase signInUserUseCase;

    private final String notionUri;

    public AuthenticationWebController(EncodingService encodingService, UserRepository userRepository,
                                       TokenService tokenService, @Value("${notion.baseuri}") String notionUri) {
        this.signInUserUseCase = new SignInUserUseCaseImpl(encodingService, userRepository, tokenService);
        this.notionUri = notionUri;
    }

    @GetMapping("signin")
    public String getSignIn() {
        return "signin";
    }

    @PostMapping("signin")
    public void signIn(UserModel userDto, HttpServletResponse servletResponse) throws IOException {
        UserDataContainer userDataContainer = UserDataContainer.builder()
                .username(userDto.username())
                .password(userDto.password())
                .build();

        String jwt = signInUserUseCase.signIn(userDataContainer);

        Cookie cookie = new Cookie("access_token", jwt);

        servletResponse.addCookie(cookie);
        servletResponse.sendRedirect("/");
    }

}
