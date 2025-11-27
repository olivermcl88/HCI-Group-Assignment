package com.hci.ufosightings.service;

import com.hci.ufosightings.common.User;
import com.hci.ufosightings.dao.UserDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserDao userDao;

    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    public User getUserById(Long id) {
        return userDao.findById(id).orElse(null);
    }

    public User getUserByUsername(String username) {
        return userDao.findByUsername(username).orElse(null);
    }

}
