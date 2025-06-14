package org.example.budgetmanager;

import org.example.budgetmanager.Configurations.JwtUtils;
import org.example.budgetmanager.Entities.AppUser;
import org.example.budgetmanager.Entities.Role;
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

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
        AppUser appUser = new AppUser(1L, "Test", "Test", "test@example.com", "password", null, null);

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
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("User created successfully"));
    }

    @Test
    public void signupShouldNotRegisterUserBecauseUserExists() throws Exception {
        //given
        AppUser appUser = new AppUser(1L, "Test", "Test", "test@example.com", "password", null, null);

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
                .andExpect(jsonPath("$.message").value("User already exists"));
    }

    @Test
    public void signInShouldAuthenticateUser() throws Exception {
        //given
        Set<Role> roles = Set.of(new Role(2L, "ROLE_USER"));
        AppUser user = new AppUser(1L, "Test", "Test", "test@example.com", "password", roles, null);
        String password = bCryptPasswordEncoder.encode("password");
        UserDetails userDetails = User.builder()
                .username("test@example.com")
                .password(password)
                .authorities("ROLE_USER")
                .build();
        Map<String, Object> otherClaims = Map.of("Roles", userDetails.getAuthorities(), "id", 1L);

        //when
        when(userService.existsByEmail(userDetails.getUsername())).thenReturn(true);
        when(userService.loadUserByUsername(userDetails.getUsername())).thenReturn(userDetails);
        when(userService.findByEmail(userDetails.getUsername())).thenReturn(Optional.of(user));

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
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.token").value("Bearer " + jwtUtils.generateToken(otherClaims, userDetails)));
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
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }

}
