package com.rafalcendrowski.AccountApplication.user;

import java.util.List;

public interface UserService {

    User loadByUsername(String username);

    User registerUser(User user);

    User updateUser(User user);

    void deleteUser(User user);

    List<User> loadAllUsers();

    boolean hasUser(String username);
}
