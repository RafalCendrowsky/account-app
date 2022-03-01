package com.rafalcendrowski.AccountApplication.user;

import java.util.List;

public interface UserService {

    public User loadByUsername(String username);

    public void saveUser(User user);

    public void deleteUser(User user);

    public List<User> loadAllUsers();

    public boolean hasUser(String username);
}
