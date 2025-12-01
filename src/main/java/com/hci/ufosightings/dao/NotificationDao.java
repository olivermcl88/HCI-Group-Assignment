package com.hci.ufosightings.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.management.Notification;
import java.util.Optional;

@Repository
public interface NotificationDao extends JpaRepository<Notification, Long> {

    Optional<Notification> findByNotiId(Long notiId);

    Optional<Notification> findByNotiName(String notiName);

}
