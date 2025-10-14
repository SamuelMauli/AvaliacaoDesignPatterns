package com.bank.service;

import com.bank.dao.UserDAO;
import com.bank.dao.UserDAOImpl;
import com.bank.model.User;

import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class AuthService {
    private UserDAO userDAO;

    public AuthService() {
        this.userDAO = new UserDAOImpl(); // Dependency injection can be used here
    }

    public User register(String username, String password, String name, String email) throws SQLException {
        if (userDAO.getUserByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists.");
        }
        if (userDAO.getUserByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already exists.");
        }
        // In a real application, password should be hashed
        User newUser = new User(UUID.randomUUID(), username, password, name, email);
        userDAO.addUser(newUser);
        return newUser;
    }

    public Optional<User> login(String username, String password) throws SQLException {
        Optional<User> userOptional = userDAO.getUserByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // In a real application, compare hashed passwords
            if (user.getPassword().equals(password)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }
}

