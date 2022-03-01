package com.rafalcendrowski.AccountApplication.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserDetailsService, UserService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username.toLowerCase(Locale.ROOT));
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        } else {
            return user;
        }
    }

    public User loadByUsername(String username) throws IllegalArgumentException {
        User user = userRepository.findByUsername(username.toLowerCase(Locale.ROOT));
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        } else {
            return user;
        }
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    public List<User> loadAllUsers() {
        return userRepository.findAll();
    }

    public boolean hasUser(String username) {
        return (userRepository.findByUsername(username.toLowerCase(Locale.ROOT)) != null);
    }
}
