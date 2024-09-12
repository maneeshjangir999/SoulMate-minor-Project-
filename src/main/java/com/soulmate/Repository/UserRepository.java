package com.soulmate.Repository;

import com.soulmate.Entites.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserInfo,Long> {
Optional<UserInfo> findByEmail(String email);
Optional<UserInfo> findByFirstname(String firstname);

}
