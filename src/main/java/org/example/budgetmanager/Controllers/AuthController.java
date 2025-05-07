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

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private final AuthenticationManager authenticationManager;

    @Autowired
    private final UserService userService;

    @Autowired
    private final RoleRepository roleRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<String> authenticateUser(@RequestBody LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwtToken = jwtUtils.generateToken(userService.loadUserByUsername(loginDto.getEmail()));
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
