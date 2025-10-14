package com.bank.dao;

import com.bank.config.DatabaseConfig;
import com.bank.model.User;
import java.sql.*;
import java.util.Optional;
import java.util.UUID;

public class UserDAOImpl implements UserDAO {

    @Override
    public void addUser(User user) throws SQLException {
        String sql = "INSERT INTO users (id, username, password, name, email) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, user.getId());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getName());
            stmt.setString(5, user.getEmail());
            stmt.executeUpdate();
        }
    }

    @Override
    public Optional<User> getUserByUsername(String username) throws SQLException {
        String sql = "SELECT id, username, password, name, email FROM users WHERE username = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new User(
                            (UUID) rs.getObject("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("name"),
                            rs.getString("email")
                    ));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> getUserById(UUID id) throws SQLException {
        String sql = "SELECT id, username, password, name, email FROM users WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new User(
                            (UUID) rs.getObject("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("name"),
                            rs.getString("email")
                    ));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> getUserByEmail(String email) throws SQLException {
        String sql = "SELECT id, username, password, name, email FROM users WHERE email = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new User(
                            (UUID) rs.getObject("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("name"),
                            rs.getString("email")
                    ));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public void updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET username = ?, password = ?, name = ?, email = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getName());
            stmt.setString(4, user.getEmail());
            stmt.setObject(5, user.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void deleteUser(UUID id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            stmt.executeUpdate();
        }
    }
}

