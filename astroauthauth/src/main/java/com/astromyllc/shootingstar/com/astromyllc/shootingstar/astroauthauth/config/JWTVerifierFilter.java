package com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.config;

import com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.model.redis.TokensEntity;
import com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.service.JwtService;
import com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.service.redis.TokensRedisService;
import com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.serviceInterface.AuthenticationServiceInterface;
import com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.serviceInterface.JwtServiceInterface;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class JWTVerifierFilter extends OncePerRequestFilter implements WebFilter {

  //  private AuthenticationManager authenticationManager;
/*
    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;

        setFilterProcessesUrl("/api/services/controller/user/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException {
        try {
            User creds = new ObjectMapper()
                    .readValue(req.getInputStream(), User.class);

            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            creds.getUsername(),
                            creds.getPassword(),
                            new ArrayList<>())
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }*/

   // private final JwtServiceInterface jwtService;
    private  TokensRedisService tokensRedisService;
    //private final AuthenticationServiceInterface authenticationServiceInterface;

  /*  public JWTVerifierFilter(TokensRedisService tokensRedisService) {
        super();
        this.tokensRedisService = tokensRedisService;
    }*/
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        final String bearerToken = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        if (StringUtils.isEmpty(bearerToken) || !StringUtils.startsWith(bearerToken, "Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        jwt = bearerToken.substring(7);
        /*
        userEmail = jwtService.extractUserName(jwt);
        if (StringUtils.isNotEmpty(userEmail)
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = authenticationServiceInterface.userDetailsService()
                    .loadUserByUsername(userEmail);
            if (jwtService.isTokenValid(jwt, userDetails)) {
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                context.setAuthentication(authToken);
                SecurityContextHolder.setContext(context);
            }
        }
        filterChain.doFilter(request, response);
*/


       // String authToken = bearerToken.replace(SecurityConstants.PREFIX, "");

        Optional<TokensEntity> tokensEntity = tokensRedisService.findById(jwt);

        if(!tokensEntity.isPresent()) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = tokensEntity.get().getAuthenticationToken();
        Jws<Claims> authClaim = Jwts.parser().setSigningKey(JwtService.genKeyValue)
                .requireIssuer("AastroAuthAuth")
                .parseClaimsJws(token);

        String username = authClaim.getBody().getSubject();

        List<Map<String, String>> authorities = (List<Map<String, String>>) authClaim.getBody().get("authorities");
        List<GrantedAuthority> grantedAuthorities = authorities.stream().map(map -> new SimpleGrantedAuthority(map.get("authority")))
                .collect(Collectors.toList());
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, grantedAuthorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        request.setAttribute("username", username);
        request.setAttribute("authorities", grantedAuthorities);
        request.setAttribute("jwt", token);

        filterChain.doFilter(request, response);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return null;
    }
}
