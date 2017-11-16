package ch.bitwise.chat.service;

import ch.bitwise.chat.entity.User;
import ch.bitwise.chat.repository.UserRepository;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@CommonsLog
public class CustomUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;

    public CustomUserDetailsService(UserRepository userRepository,
                                    @Lazy PasswordEncoder passwordEncoder,
                                    @Lazy AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByName(username);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("No user with name '%s' found.", username));
        } else {
            return user;
        }
    }

    public void changePassword(String oldPassword, String newPassword) {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        String username = currentUser.getName();

        log.debug("Re-authenticating user '"+ username + "' for due to password change.");
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, oldPassword));

        User user = (User) loadUserByUsername(username);

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void registerUser(String name, String password) {
        User user = userRepository.findByName(name);
        if (user != null) {
            throw new DuplicateUserException(String.format("User name '%s' already exists.", name));
        }

        user = new User(name, passwordEncoder.encode(password));
        userRepository.save(user);
    }

    private class UsernameNotFoundException extends RuntimeException {
        private UsernameNotFoundException(String message) {
            super(message);
        }
    }

    private class DuplicateUserException extends RuntimeException {
        private DuplicateUserException(String message) {
            super(message);
        }
    }
}
