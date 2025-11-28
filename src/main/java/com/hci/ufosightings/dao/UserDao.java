package com.hci.ufosightings.dao;

import com.hci.ufosightings.common.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDao extends JpaRepository<User, Long>{

    Optional<User> findByUsername(String username);
    Optional<User> findByUserId(Long id);

}
