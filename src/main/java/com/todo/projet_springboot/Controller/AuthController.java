package com.todo.projet_springboot.Controller;

import com.todo.projet_springboot.Config.JwtTokenUtil;
import com.todo.projet_springboot.DTO.JwtResponse;
import com.todo.projet_springboot.DTO.LoginRequest;
import com.todo.projet_springboot.DTO.RegisterRequest;
import com.todo.projet_springboot.Entity.RefreshToken;
import com.todo.projet_springboot.Entity.User;
import com.todo.projet_springboot.Repository.UserRepository;
import com.todo.projet_springboot.Service.AuthService;
import com.todo.projet_springboot.Service.RefreshTokenService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private AuthService authService;

    // Register user endpoint
    @RequestMapping(value = "/register", method = {RequestMethod.GET, RequestMethod.POST})
    public String register(@ModelAttribute RegisterRequest registerRequest, Model model) {
        if (registerRequest.getUsername() == null || registerRequest.getEmail() == null || registerRequest.getPassword() == null) {
            return "register";  // Return the register page with empty form
        } else {
            try {
                String message = authService.registerUser(registerRequest);
                model.addAttribute("message", message);  // Success message
                return "register";  // Return register page with success message
            } catch (Exception e) {
                model.addAttribute("error", "Erreur : " + e.getMessage());  // Error message
                return "register";  // Return register page with error message
            }
        }
    }

    // Login endpoint
    @RequestMapping(value = "/login", method = {RequestMethod.GET, RequestMethod.POST})
    public String login(@ModelAttribute LoginRequest loginRequest, Model model, HttpSession session) {
        if (loginRequest.getEmail() == null || loginRequest.getPassword() == null ||
                loginRequest.getEmail().isEmpty() || loginRequest.getPassword().isEmpty()) {
            return "login";  // Handle GET request or empty form fields
        } else {
            try {
                // Authenticate the user
                User user = userRepository.findByEmail(loginRequest.getEmail())
                        .orElseThrow(() -> new RuntimeException("User not found"));

                // Check if the password matches (you should hash passwords in a real system)
                if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                    model.addAttribute("error", "Invalid email or password.");
                    return "login";  // Invalid credentials, stay on login page
                }

                // Generate JWT token
                String token = jwtTokenUtil.generateToken(user);
                session.setAttribute("token", token);  // Store the token in the session

                // Add token and username to the model
                model.addAttribute("token", token);
                model.addAttribute("username", user.getUsername());

                return "redirect:/api/auth/home";  // Successful login, redirect to home page

            } catch (Exception e) {
                model.addAttribute("error", "An error occurred: " + e.getMessage());
                return "login";  // General error, stay on login page
            }
        }
    }

    // Logout endpoint
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpSession session) {
        session.invalidate();  // Invalidate the session
        return "login";  // Redirect to login page after logout
    }

    // Home page endpoint
    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        // Get the token from the session
        String token = (String) session.getAttribute("token");

        if (token == null) {
            // Logic to handle when token is null (e.g., redirect to login page, show guest view, etc.)
            model.addAttribute("message", "Bienvenue, veuillez vous connecter ou créer un compte.");
        } else {
            // Logic to handle when token is present
            String email = jwtTokenUtil.getUserEmailFromToken(token);
            String username = jwtTokenUtil.getUsernameFromToken(token);
            String role = jwtTokenUtil.getRoleFromToken(token);


            // Add user details to the model
            model.addAttribute("username", username);
            model.addAttribute("email", email);
            model.addAttribute("role", role);
            model.addAttribute("message", "Bienvenue, " + username + " !");
        }

        // Add token to the model regardless of its state (optional)
        model.addAttribute("token", token);

        // Return home page
        return "home";
    }


    // Refresh token endpoint (if needed for JWT)
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody String refreshToken) {
        try {
            // Validate the refresh token
            RefreshToken validRefreshToken = refreshTokenService.validateRefreshToken(refreshToken);

            // Generate a new JWT access token
            String newAccessToken = jwtTokenUtil.generateToken(validRefreshToken.getUser());

            return ResponseEntity.ok(new JwtResponse(newAccessToken, refreshToken, validRefreshToken.getUser().getUsername()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid refresh token");
        }
    }
}
