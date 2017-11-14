package ch.bitwise.chat.service;

import ch.bitwise.chat.entity.User;
import ch.bitwise.chat.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class UserService {
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Collection<User> findAll() {
        return userRepository.findAll();
    }

    public User findByName(String name) {
        return userRepository.findByName(name);
    }

    public User findById( Long id ) throws AccessDeniedException {
        return userRepository.findOne(id);
    }
}
