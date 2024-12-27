package com.todo.projet_springboot.Service;

import com.todo.projet_springboot.Config.JwtTokenUtil;
import com.todo.projet_springboot.DTO.LoginRequest;
import com.todo.projet_springboot.DTO.RegisterRequest;
import com.todo.projet_springboot.Entity.User;
import com.todo.projet_springboot.Enum.Role;
import com.todo.projet_springboot.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public String registerUser(RegisterRequest request) {
        // Check if the email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            return "Email already exists!";
        }

        // Create a new user entity
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        // Save the new user in the database
        userRepository.save(user);

        return "User registered successfully!";
    }
    public String authenticateUser(LoginRequest  request) {
        // Check if the user exists by email
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new RuntimeException("User not found"));

        // Check if the password matches
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // Generate JWT token
        return jwtTokenUtil.generateToken(user);
    }


}