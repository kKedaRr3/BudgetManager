package org.example.budgetmanager.Controllers;


import lombok.AllArgsConstructor;
import org.example.budgetmanager.Configurations.JwtUtils;
import org.example.budgetmanager.Dtos.LoginDto;
import org.example.budgetmanager.Dtos.SignUpDto;
import org.example.budgetmanager.Entities.AppUser;
import org.example.budgetmanager.Entities.Role;
import org.example.budgetmanager.Repositories.RoleRepository;
import org.example.budgetmanager.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final UserService userService;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<String> authenticateUser(@RequestBody LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        var userDetails = userService.loadUserByUsername(loginDto.getEmail());
        Map<String, Object> roles = Map.of("Roles", userDetails.getAuthorities());
        String jwtToken = jwtUtils.generateToken(roles, userDetails);
        return new ResponseEntity<>("Bearer " + jwtToken, HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignUpDto signUpDto) {
        if (userService.existsByEmail(signUpDto.getEmail())) {
            return new ResponseEntity<>("User already exists", HttpStatus.CONFLICT);
        }

        AppUser user = new AppUser();
        user.setFirstName(signUpDto.getFirstName());
        user.setLastName(signUpDto.getLastName());
        user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));
        user.setEmail(signUpDto.getEmail());

        Role roles = roleRepository.findByName("ROLE_USER");
        user.setRoles(Collections.singleton(roles));

        userService.save(user);

        return new ResponseEntity<>("User created successfully", HttpStatus.OK);
    }

}
