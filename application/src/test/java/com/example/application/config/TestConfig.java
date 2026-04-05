package com.example.application.config;

import com.example.application.controllers.OrderController;
import com.example.application.hibernate.BookHibImpl;
import com.example.application.hibernate.RequestHibImpl;
import com.example.application.security.JwtCheckerFilter;
import com.example.application.security.JwtService;
import com.example.application.services.BookService;
import com.example.application.services.BookShopFacade;
import com.example.application.services.SettingsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.io.IOException;
import java.util.Properties;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

@Configuration
@EnableWebMvc
public class TestConfig {

    @Bean
    @Primary
    public BookService bookService() {
        return Mockito.mock(BookService.class);
    }

    @Bean
    public SettingsService settingsServiceNotMock(){
        return new SettingsService();
    }

    @Bean
    @Primary
    public SettingsService settingsService() {
        return Mockito.mock(SettingsService.class);
    }

    @Bean
    @Primary
    public BookShopFacade bookShopFacade() {
        return Mockito.mock(BookShopFacade.class);
    }

    @Bean @Primary
    public com.example.application.security.UserDetailsService userDetailsService() {
        return Mockito.mock(
                com.example.application.security.UserDetailsService.class
        );
    }

    @Bean @Primary
    public JwtService jwtService() {
        return Mockito.mock(JwtService.class);
    }

    @Bean @Primary
    public JwtCheckerFilter jwtCheckerFilter() throws ServletException, IOException {
        JwtCheckerFilter filter = Mockito.mock(JwtCheckerFilter.class);
        // просто макировать нельзя было, потом не шла дальнейшая цепочка, это же МОК!!!!!
        // из-за того, что не было продолженния и не работал PreAuthorize!!!!!!!
        doAnswer(invocation -> {
            HttpServletRequest request = invocation.getArgument(0);

            HttpServletResponse response = invocation.getArgument(1);
            FilterChain chain = invocation.getArgument(2);

            HttpSession session = request.getSession(false);
            if (session != null) {
                SecurityContext securityContext = (SecurityContext) session
                        .getAttribute("SPRING_SECURITY_CONTEXT");
                if (securityContext != null) {

                    HttpSessionSecurityContextRepository repo =
                            new HttpSessionSecurityContextRepository();
                    repo.saveContext(securityContext, request, response);
                    request.setAttribute(
                            HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                            securityContext
                    );
                    SecurityContextHolder.setContext(securityContext);
                }
            }
            chain.doFilter(request, response);  // ← пропускаем дальше
            return null;
        }).when(filter).doFilter(any(), any(), any());

        return filter;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        Properties props = new Properties();
        props.setProperty("numberOfMonth", "3");
        props.setProperty("warehouseFunction","true");
        configurer.setProperties(props);
        return configurer;
    }


}
