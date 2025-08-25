package com.rafalcendrowski.AccountApplication.user;

import com.rafalcendrowski.AccountApplication.exceptions.CustomNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserDetailsService, UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

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
    public User loadById(Long id) throws CustomNotFoundException {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new CustomNotFoundException("User not found");
        } else {
            return optionalUser.get();
        }
    }
    /*
        split user saving to register and update so that every registered
        user will have been granted a role without it being an intrinsic
        functionality of the user class and so that it's impossible to save
        a user without registering it
    */

    @Override
    public User registerUser(User user) {
        grantRole(user);
        return userRepository.save(user);
    }

    @Override
    public User updateUser(User user) throws IllegalArgumentException {
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

    // the first user is granted the admin role
    private void grantRole(User user) {
        if (userRepository.findAll().isEmpty()) {
            user.addRole(User.Role.ADMINISTRATOR);
        } else {
            user.addRole(User.Role.USER);
        }
    }
}