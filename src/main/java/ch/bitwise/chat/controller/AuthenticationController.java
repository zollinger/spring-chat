package ch.bitwise.chat.controller;


import ch.bitwise.chat.entity.User;
import ch.bitwise.chat.entity.UserTokenState;
import ch.bitwise.chat.security.JwtAuthenticationRequest;
import ch.bitwise.chat.service.CustomUserDetailsService;
import ch.bitwise.chat.security.JwtHelper;
import lombok.Getter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping( value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE )
public class AuthenticationController {

    private JwtHelper jwtHelper;

    private AuthenticationManager authenticationManager;

    private CustomUserDetailsService userDetailsService;

    public AuthenticationController(JwtHelper jwtHelper,
                                    AuthenticationManager authenticationManager,
                                    CustomUserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtHelper = jwtHelper;
        this.userDetailsService = userDetailsService;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(
            @RequestBody JwtAuthenticationRequest authenticationRequest) throws AuthenticationException, IOException {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getUsername(),
                    authenticationRequest.getPassword()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = (User)authentication.getPrincipal();
        String jws = jwtHelper.generateToken( user.getUsername());
        return ResponseEntity.ok(new UserTokenState(jws, jwtHelper.EXPIRES));
    }

    @RequestMapping(value = "/refresh", method = RequestMethod.POST)
    public ResponseEntity<?> refreshAuthenticationToken( HttpServletRequest request, Principal principal) {

        String authToken = jwtHelper.getAuthTokenFromHeader(request);

        if (authToken != null && principal != null) {
            // TODO check user password last update
            String refreshedToken = jwtHelper.refreshToken(authToken);
            return ResponseEntity.ok(new UserTokenState(refreshedToken, jwtHelper.EXPIRES));
        } else {
            UserTokenState userTokenState = new UserTokenState();
            return ResponseEntity.accepted().body(userTokenState);
        }
    }

    @RequestMapping(value = "/change-password", method = RequestMethod.POST)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> changePassword(@RequestBody PasswordChangeRequest passwordChange) {
        userDetailsService.changePassword(passwordChange.getOldPassword(), passwordChange.getNewPassword());
        Map<String, String> result = new HashMap<>();
        result.put( "status", "ok" );
        return ResponseEntity.accepted().body(result);
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<?> register(@RequestBody UserRegistrationRequest registration) {

        userDetailsService.registerUser(registration.getName(), registration.getPassword());
        Map<String, String> result = new HashMap<>();
        result.put( "status", "ok" );
        return ResponseEntity.accepted().body(result);
    }

    @Getter
    private static class PasswordChangeRequest {
        @NotNull
        private String oldPassword;
        @NotNull
        private String newPassword;
    }

    @Getter
    private static class UserRegistrationRequest {
        @NotNull
        public String name;
        @NotNull
        public String password;
    }
}
