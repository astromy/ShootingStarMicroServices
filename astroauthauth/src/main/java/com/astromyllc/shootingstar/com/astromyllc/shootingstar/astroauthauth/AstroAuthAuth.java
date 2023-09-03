package com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth;

import com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.model.Users;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.mail.MailMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
@Slf4j
public class AstroAuthAuth {
    public static void main(String[] args) {
        SpringApplication.run(AstroAuthAuth.class,args);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }

    @Bean
    public MailMessage mailMessage(){
        return new SimpleMailMessage();
    }
    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        Set<IDialect> dialects = new HashSet<IDialect>();
        dialects.add(springSecurityDialect());
        templateEngine.setAdditionalDialects(dialects);
        templateEngine.setTemplateResolver(templateResolver());
        log.debug("Template Engine Executed");
        return templateEngine;
    }

    @Bean
    public ViewResolver viewResolver() {
        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(templateEngine());

        // viewResolver.setViewNames(new String[]{"*.html","*.xhtml"});
        log.debug("View Resolver Executed");
        return viewResolver;
    }
    public void addViewControllers(ViewControllerRegistry registry, Model model) {
        log.debug("Entering Login Page");
        model.addAttribute("users", new Users());
        registry.addViewController("/login").setViewName("login");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

    @Bean
    public SpringSecurityDialect springSecurityDialect() {
        SpringSecurityDialect dialect = new SpringSecurityDialect();
        log.debug("Spring Security Dialect Executed");
        return dialect;
    }
    @Bean
    public ClassLoaderTemplateResolver templateResolver() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("views/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML5");
        templateResolver.setOrder(1);
        log.debug("Template Resolver Executed");
        return templateResolver;
    }


}
