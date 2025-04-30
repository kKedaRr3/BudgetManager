package org.example.budgetmanager.Repositories;

import org.example.budgetmanager.Entities.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {

}
