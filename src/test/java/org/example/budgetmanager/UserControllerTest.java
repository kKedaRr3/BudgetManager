package org.example.budgetmanager;

import org.checkerframework.checker.units.qual.A;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
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
        AppUser appUser = new AppUser(1L, "admin", "admin", "admin@test.com", "admin", roles);

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
        AppUser appUser = new AppUser(1L, "user", "user", "user@test.com", "user", roles);

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
    public void shouldReturnAllUsers() throws Exception {
        //given
        adminSetup();
        List<AppUser> appUsers = List.of(new AppUser(1L, "Jan", "Kowalski", "jan@example.com", "password", null), new AppUser(2L, "Piotr", "Nowak", "piotr@example.com", "password", null));

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
    public void shouldReturnUser() throws Exception {
        //given
        AppUser appUser = new AppUser(1L, "Jan", "Kowalski", "jan@example.com", "password", null);

        //when
        when(userService.findById(appUser.getId())).thenReturn(Optional.of(appUser));

        //then
        mockMvc.perform(get("/api/users/1")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("jan@example.com"));

    }

//    TODO make more tests

}
