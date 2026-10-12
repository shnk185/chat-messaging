package org.ngs.chatmessaging.service;

import lombok.RequiredArgsConstructor;
import org.ngs.chatmessaging.entity.UserEntity;
import org.ngs.chatmessaging.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserEntity createUser(String userName) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserName(userName);
        return userRepository.save(userEntity);
    }

    public List<UserEntity> fetchAllUsers() {
        return userRepository.findAll();
    }
}
