package ch.bitwise.chat.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
public class JwtHelper {

    @Value("${app.name}")
    private String APP_NAME;

    @Value("${app.token.secret}")
    public String SECRET;

    @Value("${app.token.expires}")
    public int EXPIRES;

    @Value("${app.token.header}")
    private String TOKEN_HEADER;

    private SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;

    public Date now() {
        return new Date();
    }

    public String getUsernameFromToken(String token) {
        String username;
        try {
            Claims claims = this.getClaimsFromToken(token);
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    public String refreshToken(String token) {
        String refreshedToken;
        try {
            Claims claims = this.getClaimsFromToken(token);
            claims.setIssuedAt(now());
            refreshedToken = Jwts.builder()
                .setClaims(claims)
                .setExpiration(getExpirationDate())
                .signWith( SIGNATURE_ALGORITHM, SECRET )
                .compact();
        } catch (Exception e) {
            refreshedToken = null;
        }
        return refreshedToken;
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuer( APP_NAME )
                .setIssuedAt(now())
                .setExpiration(getExpirationDate())
                .signWith( SIGNATURE_ALGORITHM, SECRET )
                .compact();
    }

    private Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

    private Date getExpirationDate() {
        return new Date(now().getTime() + EXPIRES * 1000);
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        // TODO: We should also check whether the token has been created before the last password reset here...
        return (
                username != null &&
                username.equals(userDetails.getUsername())
        );
    }

    public String getAuthHeaderFromHeader( HttpServletRequest request ) {
        return request.getHeader(TOKEN_HEADER);
    }

}