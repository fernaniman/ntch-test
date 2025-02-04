package org.example.Repository;

import org.example.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<UserEntity, Integer> {

    UserEntity findByUsername(String username);

    UserEntity findByEmail(String email);
}
