package org.example.budgetmanager;

import org.example.budgetmanager.Configurations.JwtUtils;
import org.example.budgetmanager.Entities.AppUser;
import org.example.budgetmanager.Services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    @MockitoBean
    private UserService userService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Test
    public void signupShouldRegisterUser() throws Exception {
        //given
        AppUser appUser = new AppUser(1L, "Test", "Test", "test@example.com", "password", null);

        //when
        when(userService.existsByEmail(appUser.getEmail())).thenReturn(false);
        when(userService.save(any(AppUser.class))).thenReturn(appUser);

        //then
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                      "firstName": "Test",
                                      "lastName": "Test",
                                      "email": "test@example.com",
                                      "password": "password"
                                    }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().string("User created successfully"));
    }

    @Test
    public void signupShouldNotRegisterUserBecauseUserExists() throws Exception {
        //given
        AppUser appUser = new AppUser(1L, "Test", "Test", "test@example.com", "password", null);

        //when
        when(userService.existsByEmail(appUser.getEmail())).thenReturn(true);

        //then
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                      "firstName": "Test",
                                      "lastName": "Test",
                                      "email": "test@example.com",
                                      "password": "password"
                                    }
                                """))
                .andExpect(status().isConflict())
                .andExpect(content().string("User already exists"));
    }

    @Test
    public void signInShouldAuthenticateUser() throws Exception {
        //given
        String password = bCryptPasswordEncoder.encode("password");
        UserDetails userDetails = User.builder()
                .username("test@example.com")
                .password(password)
                .authorities("ROLE_USER")
                .build();

        //when
        when(userService.existsByEmail(userDetails.getUsername())).thenReturn(true);
        when(userService.loadUserByUsername(userDetails.getUsername())).thenReturn(userDetails);

        //then
        mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                            {
                              "email": "test@example.com",
                              "password": "password"
                            }
                        """))
                .andExpect(status().isOk())
                .andExpect(content().string("Bearer " + jwtUtils.generateToken(Collections.singletonMap("Roles", userDetails.getAuthorities()), userDetails)));
    }

    @Test
    public void signInShouldNotAuthenticateUserBecauseUserDoesNotExist() throws Exception {
        //given
        String password = bCryptPasswordEncoder.encode("password");
        UserDetails userDetails = User.builder()
                .username("test@example.com")
                .password(password)
                .authorities("ROLE_USER")
                .build();

        //when
        when(userService.existsByEmail(userDetails.getUsername())).thenReturn(false);

        //then
        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "email": "test@example.com",
                              "password": "password"
                            }
                        """))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid credentials"));
    }

}
