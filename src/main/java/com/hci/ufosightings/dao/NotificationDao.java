package com.hci.ufosightings.dao;

import com.hci.ufosightings.common.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationDao extends JpaRepository<Notification, Long> {

    Optional<Notification> findByNotiId(Long notiId);

    List<Notification> findByUserId(Long userId);

}
