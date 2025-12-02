package com.hci.ufosightings.service;

import com.hci.ufosightings.common.Notification;
import com.hci.ufosightings.dao.NotificationDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationDao notificationDao;

    public List<Notification> getAllNotis() {
        return notificationDao.findAll();
    }

    public List<Notification> getAllNotisByUserId(Long userId) {
        return notificationDao.findByUserId(userId);
    }
}
