package ch.bitwise.chat.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;


@Getter
@Setter
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private String token;
    private final UserDetails principle;

    public JwtAuthenticationToken(UserDetails principle ) {
        super( principle.getAuthorities() );
        this.principle = principle;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public UserDetails getPrincipal() {
        return principle;
    }

}
