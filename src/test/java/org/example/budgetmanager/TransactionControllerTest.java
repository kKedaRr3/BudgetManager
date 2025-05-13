package org.example.budgetmanager;

import org.example.budgetmanager.Configurations.JwtUtils;
import org.example.budgetmanager.Entities.AppUser;
import org.example.budgetmanager.Entities.Category;
import org.example.budgetmanager.Entities.Transaction;
import org.example.budgetmanager.Services.CategoryService;
import org.example.budgetmanager.Services.TransactionService;
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
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    @MockitoBean
    private AuthUtils authUtils;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private TransactionService transactionService;

    private String jwtToken;

    private Category category;

    @BeforeEach
    public void setup() {
        AppUser appUser = new AppUser(1L, "Jan", "Kowalski", "test@example.com", "password", null);
        category = new Category(1L, "Zywnosc", appUser);

        UserDetails userDetails = User.builder()
                .username("test@example.com")
                .password("password")
                .authorities("ROLE_USER")
                .build();

        when(userService.findByEmail(appUser.getEmail())).thenReturn(Optional.of(appUser));
        when(userService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
        when(authUtils.getLoggedInUser()).thenReturn(appUser);
        when(categoryService.getCategoryByUserIdAndId(appUser.getId(), category.getId())).thenReturn(Optional.of(category));

        jwtToken = jwtUtils.generateToken(userDetails);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    @Test
    public void getShouldReturnAllTransactions() throws Exception {
        //given
        List<Transaction> transactions = List.of(new Transaction(1L, 50.0, "Lidl", category), new Transaction(2L, 10.59, "Studenciak", category));

        //when
        when(transactionService.getAllTransactionsByCategoryId(category.getId())).thenReturn(transactions);

        //then
        mockMvc.perform(get("/api/transactions/1")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].amount").value(50.0))
                .andExpect(jsonPath("$[0].description").value("Lidl"));
    }

    @Test
    public void getAllTransactionsShouldReturnHttp404() throws Exception {
        //given
        List<Transaction> transactions = List.of();

        //when
        when(transactionService.getAllTransactionsByCategoryId(category.getId())).thenReturn(transactions);

        //then
        mockMvc.perform(get("/api/transactions/1")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getShouldReturnTransactionById() throws Exception {
        //given
        Transaction transaction = new Transaction(1L, 50.0, "Lidl", category);

        //when
        when(transactionService.getTransactionByCategoryIdAndId(category.getId(), 1L)).thenReturn(Optional.of(transaction));

        //then
        mockMvc.perform(get("/api/transactions/1/1")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(50.0))
                .andExpect(jsonPath("$.description").value("Lidl"));
    }

    @Test
    public void getShouldReturnHttp404() throws Exception {
        //given
        Long transactionId = 1L;

        //when
        when(transactionService.getTransactionByCategoryIdAndId(category.getId(), transactionId)).thenReturn(Optional.empty());

        //then
        mockMvc.perform(get("/api/transactions/1/1")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    public void postShouldAddTransaction() throws Exception {
        //given
        Transaction savedTransaction = new Transaction(1L, 50.99, "Lidl", category);

        //when
        when(transactionService.save(any(Transaction.class))).thenReturn(savedTransaction);

        //then
        mockMvc.perform(post("/api/transactions/1")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                      "amount": "50.99",
                                      "description": "Lidl"
                                    }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(50.99))
                .andExpect(jsonPath("$.description").value("Lidl"));
    }

    @Test
    public void putShouldUpdateCategory() throws Exception {

        // given
        Long transactionId = 2L;

        Transaction existingTransaction = new Transaction(transactionId, 50.99, "OldTransaction", category);
        Transaction updatedTransaction = new Transaction(transactionId, 60.99, "NewTransaction", category);

        // when
        when(transactionService.getTransactionByCategoryIdAndId(category.getId(), transactionId))
                .thenReturn(Optional.of(existingTransaction));
        when(transactionService.save(any(Transaction.class))).thenReturn(updatedTransaction);

        // then
        mockMvc.perform(put("/api/transactions/1/{id}", transactionId)
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                      "amount" : "60.99",
                                      "description" : "NewTransaction"                               \s
                                    }
                               \s"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.amount").value("60.99"))
                .andExpect(jsonPath("$.description").value("NewTransaction"));
    }

    @Test
    public void putShouldReturnHttp404() throws Exception {

        // given
        Long transactionId = 2L;

        // when
        when(transactionService.getTransactionByCategoryIdAndId(category.getId(), transactionId))
                .thenReturn(Optional.empty());

        // then
        mockMvc.perform(put("/api/transactions/1/{id}", transactionId)
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                      "amount" : "60.99",
                                      "description" : "NewTransaction"                               \s
                                    }
                               \s"""))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteShouldDeleteTransaction() throws Exception {
        // given
        Long transactionId = 2L;
        Transaction transaction = new Transaction(transactionId, 50.99, "Transaction", category);

        //when
        when(transactionService.getTransactionByCategoryIdAndId(category.getId(), transactionId)).thenReturn(Optional.of(transaction));

        //then
        mockMvc.perform(delete("/api/transactions/1/{id}", transactionId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteShouldReturnHttp404() throws Exception {
        // given
        Long transactionId = 2L;

        //when
        when(transactionService.getTransactionByCategoryIdAndId(category.getId(), transactionId)).thenReturn(Optional.empty());

        //then
        mockMvc.perform(delete("/api/transactions/1/{id}", transactionId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnHttp403() throws Exception {
        //given
        SecurityContextHolder.clearContext();

        //when
        //then
        mockMvc.perform(get("/api/transactions/1"))
                .andExpect(status().isForbidden());
    }

}
