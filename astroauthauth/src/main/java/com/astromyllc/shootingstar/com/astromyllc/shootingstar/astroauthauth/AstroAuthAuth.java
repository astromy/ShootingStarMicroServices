package com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.MailMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableDiscoveryClient
@Slf4j
public class AstroAuthAuth {
    public static void main(String[] args) {
        SpringApplication.run(AstroAuthAuth.class,args);
    }


    @Bean
    public MailMessage mailMessage(){
        return new SimpleMailMessage();
    }


    @Bean("1")
    public PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder(16, 32, 1, 60000, 10);
        //return  new BCryptPasswordEncoder();
    }

    /* @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        Set<IDialect> dialects = new HashSet<IDialect>();
        dialects.add(springSecurityDialect());
        templateEngine.setAdditionalDialects(dialects);
        templateEngine.setTemplateResolver(templateResolver());
        log.debug("Template Engine Executed");
        return templateEngine;
    }*/

   /* @Bean
    public ViewResolver viewResolver() {
        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(templateEngine());

        // viewResolver.setViewNames(new String[]{"*.html","*.xhtml"});
        log.debug("View Resolver Executed");
        return viewResolver;
    }*/
   /* public void addViewControllers(ViewControllerRegistry registry, Model model) {
        log.debug("Entering Login Page");
        model.addAttribute("users", new Users());
        registry.addViewController("/login").setViewName("login");
       // registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }*/

   /* @Bean
    public SpringSecurityDialect springSecurityDialect() {
        SpringSecurityDialect dialect = new SpringSecurityDialect();
        log.debug("Spring Security Dialect Executed");
        return dialect;
    }*/
   /* @Bean
    public ClassLoaderTemplateResolver templateResolver() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("views/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML5");
        templateResolver.setOrder(1);
        log.debug("Template Resolver Executed");
        return templateResolver;
    }*/


}
