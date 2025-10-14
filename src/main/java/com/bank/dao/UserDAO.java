package com.bank.dao;

import com.bank.model.User;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public interface UserDAO {
    void addUser(User user) throws SQLException;
    Optional<User> getUserByUsername(String username) throws SQLException;
    Optional<User> getUserById(UUID id) throws SQLException;
    Optional<User> getUserByEmail(String email) throws SQLException;
    void updateUser(User user) throws SQLException;
    void deleteUser(UUID id) throws SQLException;
}

