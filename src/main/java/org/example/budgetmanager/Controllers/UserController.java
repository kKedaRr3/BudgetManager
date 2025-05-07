package org.example.budgetmanager.Controllers;

import lombok.AllArgsConstructor;
import org.example.budgetmanager.Entities.AppUser;
import org.example.budgetmanager.Services.UserService;
import org.example.budgetmanager.dtos.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

//    Useless
    @GetMapping("")
    public ResponseEntity<List<UserDto>> getUsers() {
        var userList = userService.findAll().stream().map(user -> new UserDto(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail())).toList();
        if (userList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        var user = userService.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        UserDto userDto = new UserDto(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail());
        return ResponseEntity.ok(userDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody AppUser appUser) {
        var userToUpdate = userService.findById(id).orElse(null);
        if (userToUpdate == null) {
            return ResponseEntity.notFound().build();
        }
        appUser.setId(id);
        userService.save(appUser);
        UserDto userDto = new UserDto(appUser.getId(), appUser.getFirstName(), appUser.getLastName(), appUser.getEmail());
        return ResponseEntity.ok(userDto);
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
