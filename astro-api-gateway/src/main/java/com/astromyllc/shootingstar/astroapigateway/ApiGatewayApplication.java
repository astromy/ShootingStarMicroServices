package com.astromyllc.shootingstar.astroapigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {

   /* @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("setup", r -> r.path("/api/setup/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://shootingStar-setup"))

                .route("online-application", r -> r.path("/api/applications/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://online-application"))

                .route("HR", r -> r.path("/api/hr/**")
                .filters(f -> f.stripPrefix(1))
                .uri("lb://HR-Service"))


                .route("finance", r -> r.path("/api/finance/**")
                .filters(f -> f.stripPrefix(1))
                .uri("lb://finance"))


                .route("clinic", r -> r.path("/api/clinic/**")
                .filters(f -> f.stripPrefix(1))
                .uri("lb://clinic"))


                .route("stores-inventory", r -> r.path("/api/stores_inventory/**")
                .filters(f -> f.stripPrefix(1))
                .uri("lb://stores-inventory"))


                .route("academics", r -> r.path("/api/academics/**")
                .filters(f -> f.stripPrefix(1))
                .uri("lb://shootingStar-academics"))


                .route("administration-pta", r -> r.path("/api/administration-pta/**")
                .filters(f -> f.stripPrefix(1))
                .uri("lb://administration-pta"))


                .route("astroauthauth", r -> r.path("/api/astroauthauth/**")
                .filters(f -> f.stripPrefix(1))
                .uri("lb://astroauthauth"))


                .route("discovery_server", r -> r.path("/eureka/web")
                .filters(f -> f.stripPrefix(1).setPath("/"))
                .uri("http://localhost:8761"))


                .route("discovery_server_static", r -> r.path("/eureka/**")
                .filters(f -> f.stripPrefix(1))
                .uri("http://localhost:8761"))
                .build();
         }*/

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
