package org.example.budgetmanager.Services;

import org.example.budgetmanager.Entities.AppUser;
import org.example.budgetmanager.Repositories.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(email + " not found."));

        Set<GrantedAuthority> authorities = user
                .getRoles()
                .stream()
                .map((role) -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toSet());

        return new User(user.getEmail(), user.getPassword(), authorities);
    }

    public List<AppUser> findAll() {
        return this.userRepository.findAll();
    }

    public Optional<AppUser> findById(Long id) {
        return this.userRepository.findById(id);
    }

    public Optional<AppUser> findByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    public boolean existsByEmail(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public AppUser save(AppUser user) {
        return this.userRepository.save(user);
    }

    public void delete(AppUser user) {
        this.userRepository.delete(user);
    }

}
