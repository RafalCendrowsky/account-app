package com.rafalcendrowski.AccountApplication.user;

import com.rafalcendrowski.AccountApplication.exceptions.CustomNotFoundException;
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
    public User loadByUsername(String username) throws CustomNotFoundException {
        User user = userRepository.findByUsername(username.toLowerCase(Locale.ROOT));
        if (user == null) {
            throw new CustomNotFoundException("User not found");
        } else {
            return user;
        }
    }

    @Override
    public User registerUser(User user) {
        grantRole(user);
        return userRepository.save(user);
    }

    @Override
    public User updateUser(User user) throws IllegalArgumentException{
        if (hasUser(user.getUsername())) {
            return userRepository.save(user);
        } else {
            throw new IllegalArgumentException("User does not exist");
        }
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

    @Override
    public void grantRole(User user) {
        if (userRepository.findAll().size() == 0) {
            user.addRole(User.Role.ADMINISTRATOR);
        } else {
            user.addRole(User.Role.USER);
        }
    }
}