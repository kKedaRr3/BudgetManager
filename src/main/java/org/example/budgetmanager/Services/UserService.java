package org.example.budgetmanager.Services;

import org.example.budgetmanager.Entities.AppUser;
import org.example.budgetmanager.Repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public List<AppUser> findAll() {
        return this.userRepository.findAll();
    }

    public Optional<AppUser> findById(Long id) {
        return this.userRepository.findById(id);
    }

    public AppUser save(AppUser user) {
        return this.userRepository.save(user);
    }

    public void delete(AppUser user) {
        this.userRepository.delete(user);
    }

}
