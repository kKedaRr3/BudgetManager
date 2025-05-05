package org.example.budgetmanager.Controllers;

import lombok.AllArgsConstructor;
import org.example.budgetmanager.Entities.AppUser;
import org.example.budgetmanager.Services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

//    Useless
    @GetMapping("")
    public ResponseEntity<List<AppUser>> getUsers() {
        var userList = userService.findAll();
        if (userList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppUser> getUser(@PathVariable Long id) {
        var user = userService.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppUser> updateUser(@PathVariable Long id, @RequestBody AppUser appUser) {
        var userToUpdate = userService.findById(id).orElse(null);
        if (userToUpdate == null) {
            return ResponseEntity.notFound().build();
        }
        appUser.setId(id);
        userService.save(appUser);
        return ResponseEntity.ok(appUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        var user = userService.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        userService.delete(user);
        return ResponseEntity.noContent().build();
    }

}
