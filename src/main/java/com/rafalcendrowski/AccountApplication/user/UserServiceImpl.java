package com.rafalcendrowski.AccountApplication.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class UserServiceImpl implements UserDetailsService, UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username.toLowerCase(Locale.ROOT));
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        } else {
            return user;
        }
    }

    @Override
    public User loadByUsername(String username) throws IllegalArgumentException {
        User user = userRepository.findByUsername(username.toLowerCase(Locale.ROOT));
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        } else {
            return user;
        }
    }

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    @Override
    public List<User> loadAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public boolean hasUser(String username) {
        return (userRepository.findByUsername(username.toLowerCase(Locale.ROOT)) != null);
    }
}
