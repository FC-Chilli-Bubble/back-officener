package fastcampus.team7.Livable_officener.global.sercurity;

import fastcampus.team7.Livable_officener.global.constant.JwtExceptionCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtProvider {

    @Value("${jwt.secret.key}")
    private String salt;

    private Key secretKey;

    private final long exp = 1000L * 60 * 60;

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private static final String BEARER_TOKEN_PREFIX = "Bearer ";

    private final CustomUserDetailsService userDetailsService;

    @PostConstruct
    protected void init() {
        secretKey = Keys.hmacShaKeyFor(salt.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(String email) {
        Claims claims = Jwts.claims().setSubject(email);
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + exp))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getEmail(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getEmail(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String resolveToken(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION_HEADER);
    }

    public boolean validateToken(String token) {
        try {
            if (!token.startsWith(BEARER_TOKEN_PREFIX)) {
                return false;
            }
            token = token.split(" ")[1].trim();

            Jws<Claims> claims = Jwts
                    .parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);

            return !claims.getBody().getExpiration().before(new Date());
        } catch (SignatureException e) {
            log.info("JwtProvider SignatureException 예외 발생");
            throw new JwtException(JwtExceptionCode.WRONG_TYPE_TOKEN.getMessage());
        } catch (MalformedJwtException e) {
            log.info("JwtProvider MalformedJwtException 예외 발생");
            throw new JwtException(JwtExceptionCode.UNSUPPORTED_TOKEN.getMessage());
        } catch (ExpiredJwtException e) {
            log.info("JwtProvider ExpiredJwtException 예외 발생");
            throw new JwtException(JwtExceptionCode.EXPIRED_TOKEN.getMessage());
        } catch (IllegalArgumentException e) {
            log.info("JwtProvider IllegalArgumentException 예외 발생");
            throw new JwtException(JwtExceptionCode.UNKNOWN_ERROR.getMessage());
        }
    }


}
