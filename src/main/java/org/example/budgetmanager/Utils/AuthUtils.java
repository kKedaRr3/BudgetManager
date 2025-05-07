package org.example.budgetmanager.Utils;

import org.example.budgetmanager.Entities.AppUser;
import org.example.budgetmanager.Services.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class AuthUtils {

    public static AppUser getLoggedInUser(UserService userService){
        String currentUserEmail = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        return userService.findByEmail(currentUserEmail).orElseThrow(() -> new RuntimeException("User not found"));
    }

}
