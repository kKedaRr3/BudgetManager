package org.example.budgetmanager.Utils;

import lombok.RequiredArgsConstructor;
import org.example.budgetmanager.Entities.AppUser;
import org.example.budgetmanager.Services.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class AuthUtils {

    private final UserService userService;

    public AppUser getLoggedInUser(){
        String currentUserEmail = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

        Optional<AppUser> user = userService.findByEmail(currentUserEmail);
        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        else {
            return user.get();
        }
    }

}
