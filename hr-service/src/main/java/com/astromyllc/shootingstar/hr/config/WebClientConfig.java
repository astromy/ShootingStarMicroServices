package com.astromyllc.shootingstar.hr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class WebClientConfig {

    @Bean
    //@LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(20 * 1024 * 1024));

    }

    /*@Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.zoho.com");
        mailSender.setPort(587);
        mailSender.setUsername("orb@astromyllc.com");
        mailSender.setPassword("1ag4bA16WnAY");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "*");
        props.put("mail.debug", "true"); // Disable in production

        // Force Jakarta Mail implementation
        props.put("mail.smtp.provider.class", "com.sun.mail.smtp.SMTPProvider");
        props.put("mail.smtp.class", "com.sun.mail.smtp.SMTPTransport");

        return mailSender;
    }*/



    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(10); // Configure the thread pool size based on your needs
    }
}
