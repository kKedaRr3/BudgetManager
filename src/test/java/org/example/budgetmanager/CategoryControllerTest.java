package org.example.budgetmanager;

import org.example.budgetmanager.Configurations.JwtUtils;
import org.example.budgetmanager.Entities.AppUser;
import org.example.budgetmanager.Entities.Category;
import org.example.budgetmanager.Services.CategoryService;
import org.example.budgetmanager.Services.UserService;
import org.example.budgetmanager.Utils.AuthUtils;
import org.junit.jupiter.api.BeforeEach;
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
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    @MockitoBean
    private AuthUtils authUtils;

    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private UserService userService;

    private String jwtToken;

    private AppUser appUser;

    @BeforeEach
    public void setup() {
        appUser = new AppUser(1L, "Jan", "Kowalski", "test@example.com", "password", null);

        UserDetails userDetails = User.builder()
                .username("test@example.com")
                .password("password")
                .authorities("ROLE_USER")
                .build();

        when(userService.findByEmail(appUser.getEmail())).thenReturn(Optional.of(appUser));
        when(userService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
        when(authUtils.getLoggedInUser()).thenReturn(appUser);

        jwtToken = jwtUtils.generateToken(userDetails);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    @Test
    public void getShouldReturnAllCategories() throws Exception {

        //given
        List<Category> categories = List.of(new Category(1L, "Transport", appUser), new Category(2L, "Zywnosc", appUser));

        //when
        when(categoryService.getAllCategoriesByUserId(appUser.getId())).thenReturn(categories);

        //then
        mockMvc.perform(get("/api/categories")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Transport"));
    }

    @Test
    public void getAllCategoriesShouldReturnHttp404() throws Exception {
        //given
        List<Category> categories = List.of();

        //when
        when(categoryService.getAllCategoriesByUserId(appUser.getId())).thenReturn(categories);

        //then
        mockMvc.perform(get("/api/categories")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getShouldReturnCategoryById() throws Exception {
        //given
        Category category = new Category(2L, "Zywnosc", appUser);

        //when
        when(categoryService.getCategoryByUserIdAndId(appUser.getId(), 2L)).thenReturn(Optional.of(category));

        //then
        mockMvc.perform(get("/api/categories/2")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Zywnosc"));
    }

    @Test
    public void getShouldReturnHttp404() throws Exception {
        //given

        //when
        when(categoryService.getCategoryByUserIdAndId(appUser.getId(), 2L)).thenReturn(Optional.empty());

        //then
        mockMvc.perform(get("/api/categories/2")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    public void postShouldAddCategory() throws Exception {
        //given
        Category savedCategory = new Category(2L, "Zywnosc", appUser);

        //when
        when(categoryService.save(any(Category.class))).thenReturn(savedCategory);

        //then
        mockMvc.perform(post("/api/categories")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                      "name": "Zywnosc"
                                    }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Zywnosc"));
    }

    @Test
    public void putShouldUpdateCategory() throws Exception {
        // given
        Long categoryId = 2L;

        Category existingCategory = new Category(categoryId, "OldName", appUser);
        Category updatedCategory = new Category(categoryId, "UpdatedName", appUser);

        // when
        when(categoryService.getCategoryByUserIdAndId(appUser.getId(), categoryId))
                .thenReturn(Optional.of(existingCategory));
        when(categoryService.save(any(Category.class))).thenReturn(updatedCategory);

        // then
        mockMvc.perform(put("/api/categories/{id}", categoryId)
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                      "name": "UpdatedName"
                                    }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("UpdatedName"))
                .andExpect(jsonPath("$.id").value(2));
    }

    @Test
    public void putShouldReturnHttp404() throws Exception {
        // given
        Long categoryId = 2L;

        // when
        when(categoryService.getCategoryByUserIdAndId(appUser.getId(), categoryId))
                .thenReturn(Optional.empty());

        // then
        mockMvc.perform(put("/api/categories/{id}", categoryId)
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                      "name": "UpdatedName"
                                    }
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteShouldDeleteCategory() throws Exception {
        // given
        Long categoryId = 2L;
        Category category = new Category(categoryId, "Transport", appUser);

        // when
        when(categoryService.getCategoryByUserIdAndId(appUser.getId(), categoryId))
                .thenReturn(Optional.of(category));

        // then
        mockMvc.perform(delete("/api/categories/{id}", categoryId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteShouldReturnHttp404() throws Exception {
        // given
        Long categoryId = 2L;

        // when
        when(categoryService.getCategoryByUserIdAndId(appUser.getId(), categoryId))
                .thenReturn(Optional.empty());

        // then
        mockMvc.perform(delete("/api/categories/{id}", categoryId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnHttp403() throws Exception {
//        given
        SecurityContextHolder.clearContext();
//        when

//        then
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name" : "Transport"
                                }"""))
                .andExpect(status().isForbidden());
    }
}
