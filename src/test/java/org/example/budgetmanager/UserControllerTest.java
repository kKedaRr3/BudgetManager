package org.example.budgetmanager;

import org.example.budgetmanager.Configurations.JwtUtils;
import org.example.budgetmanager.Entities.AppUser;
import org.example.budgetmanager.Entities.Role;
import org.example.budgetmanager.Services.UserService;
import org.example.budgetmanager.Utils.AuthUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AuthUtils authUtils;

    private String jwtToken;

    public void adminSetup() {
        SecurityContextHolder.clearContext();

        Set<Role> roles = Set.of(new Role(2L, "ROLE_ADMIN"));
        AppUser appUser = new AppUser(1L, "admin", "admin", "admin@test.com", "admin", roles, null);

        UserDetails userDetails = User.builder()
                .username("admin@test.com")
                .password("admin")
                .authorities("ROLE_ADMIN")
                .build();

        when(userService.findByEmail(appUser.getEmail())).thenReturn(Optional.of(appUser));
        when(userService.loadUserByUsername("admin@test.com")).thenReturn(userDetails);
        when(authUtils.getLoggedInUser()).thenReturn(appUser);

        Map<String, Object> rolesClaims = Map.of("Roles", userDetails.getAuthorities());
        jwtToken = jwtUtils.generateToken(rolesClaims, userDetails);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

    }

    @BeforeEach
    public void setup() {
        Set<Role> roles = Set.of(new Role(1L, "ROLE_USER"));
        AppUser appUser = new AppUser(1L, "user", "user", "user@test.com", "user", roles, null);

        UserDetails userDetails = User.builder()
                .username("user@test.com")
                .password("user")
                .authorities("ROLE_USER")
                .build();

        when(userService.findByEmail(appUser.getEmail())).thenReturn(Optional.of(appUser));
        when(userService.loadUserByUsername("user@test.com")).thenReturn(userDetails);
        when(authUtils.getLoggedInUser()).thenReturn(appUser);

        Map<String, Object> rolesClaims = Map.of("Roles", userDetails.getAuthorities());
        jwtToken = jwtUtils.generateToken(rolesClaims, userDetails);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

    }

    @Test
    public void getShouldReturnAllUsers() throws Exception {
        //given
        adminSetup();
        List<AppUser> appUsers = List.of(new AppUser(1L, "Jan", "Kowalski", "jan@example.com", "password", null, null), new AppUser(2L, "Piotr", "Nowak", "piotr@example.com", "password", null, null));

        //when
        when(userService.findAll()).thenReturn(appUsers);

        //then
        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].firstName").value("Jan"))
                .andExpect(jsonPath("$[1].firstName").value("Piotr"))
                .andExpect(jsonPath("$[0].email").value("jan@example.com"))
                .andExpect(jsonPath("$[1].email").value("piotr@example.com"));
    }

    @Test
    public void getAllUsersShouldReturnHttp404() throws Exception {
        //given
        adminSetup();
        List<AppUser> appUsers = List.of();

        //when
        when(userService.findAll()).thenReturn(appUsers);

        //then
        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getShouldReturnUser() throws Exception {
        //given
        AppUser appUser = new AppUser(1L, "Jan", "Kowalski", "jan@example.com", "password", null, null);

        //when
        when(userService.findById(appUser.getId())).thenReturn(Optional.of(appUser));

        //then
        mockMvc.perform(get("/api/users/1")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("jan@example.com"));
    }

    @Test
    public void getShouldReturnHttp403() throws Exception {
        //given
        Long userId = 1L;

        //when
        when(userService.findById(userId)).thenReturn(Optional.empty());

        //then
        mockMvc.perform(get("/api/users/{id}", userId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    public void putShouldUpdateUser() throws Exception {
        //given
        Long userId = 1L;
        AppUser existingUser = new AppUser(userId, "existing", "existing", "test@gmail.com", "password", null, null);
        AppUser updatedUser = new AppUser(userId, "updated", "updated", "test@gmail.com", "updated", null, null);

        //when
        when(userService.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userService.save(any(AppUser.class))).thenReturn(updatedUser);

        //then
        mockMvc.perform(put("/api/users/1")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                      "firstName": "updated",
                                      "lastName": "updated",
                                      "email": "test@gmail.com",
                                      "password": "updated"
                                    }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("updated"))
                .andExpect(jsonPath("$.lastName").value("updated"))
                .andExpect(jsonPath("$.email").value("test@gmail.com"));

    }

    @Test
    public void putShouldReturnHttp404() throws Exception {
        //given
        Long userId = 1L;

        //when
        when(userService.findById(userId)).thenReturn(Optional.empty());

        //then
        mockMvc.perform(put("/api/users/{id}", userId)
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                      "firstName": "updated",
                                      "lastName": "updated",
                                      "email": "test@gmail.com",
                                      "password": "updated"
                                    }
                                """))
                .andExpect(status().isNotFound());
    }


    @Test
    public void deleteShouldDeleteUser() throws Exception {
        //given
        Long userId = 1L;
        AppUser appUser = new AppUser(userId, "test", "test", "test@gmail.com", "password", null, null);

        //when
        when(userService.findById(userId)).thenReturn(Optional.of(appUser));

        //then
        mockMvc.perform(delete("/api/users/{id}", userId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteShouldReturnHttp404() throws Exception {
        //given
        Long userId = 1L;

        //when
        when(userService.findById(userId)).thenReturn(Optional.empty());

        //then
        mockMvc.perform(delete("/api/users/{id}", userId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getShouldReturnHttp403BecauseIdsDiffer() throws Exception {
        //given
        //Logged-in user's ID is 1

        //when
        //then
        mockMvc.perform(get("/api/users/3")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isForbidden());

    }

    @Test
    public void getAllUsersShouldReturnHttp403() throws Exception {
        //given
        //Logged-in user is not admin

        //when
        //then
        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isForbidden());

    }

    @Test
    public void putShouldReturnHttp403BecauseIdsDiffer() throws Exception {
        //given
        //Logged-in user's ID is 1

        //when
        //then
        mockMvc.perform(put("/api/users/3")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                      "firstName": "updated",
                                      "lastName": "updated",
                                      "email": "test@gmail.com",
                                      "password": "updated"
                                    }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deleteShouldReturnHttp403BecauseIdsDiffer() throws Exception {
        //given
        //Logged-in user's ID is 1

        //when
        //then
        mockMvc.perform(delete("/api/users/3")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isForbidden());
    }
}
