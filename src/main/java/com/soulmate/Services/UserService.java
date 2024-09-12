package com.soulmate.Services;

import com.soulmate.Entites.UserInfo;
import org.springframework.ui.Model;

import java.util.Optional;

public interface UserService {
public void createUser(UserInfo userInfo);
Optional<UserInfo>FindByUsername(String firstname);

Optional<UserInfo>FindByEmail(String email);

}
