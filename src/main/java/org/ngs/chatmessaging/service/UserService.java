package org.ngs.chatmessaging.service;

import org.ngs.chatmessaging.entity.UserEntity;
import org.ngs.chatmessaging.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserEntity createUser(String userName) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserName(userName);
        return userRepository.save(userEntity);
    }

    public List<UserEntity> fetchAllUsers(Long userId) {
        return userRepository.findAll();
    }
}
