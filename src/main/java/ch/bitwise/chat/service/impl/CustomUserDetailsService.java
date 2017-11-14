package ch.bitwise.chat.service.impl;

import ch.bitwise.chat.entity.User;
import ch.bitwise.chat.repository.UserRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final Log LOGGER = LogFactory.getLog(getClass());

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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByName(username);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
        } else {
            return user;
        }
    }

    public void changePassword(String oldPassword, String newPassword) {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        String username = currentUser.getName();

        LOGGER.debug("Re-authenticating user '"+ username + "' for due to password change.");
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, oldPassword));

        User user = (User) loadUserByUsername(username);

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void registerUser(String name, String password) {
        User user = userRepository.findByName(name);
        if (user != null) {
            throw new RuntimeException("User name already exists.");
        }

        user = new User(name, passwordEncoder.encode(password));
        userRepository.save(user);
    }
}
