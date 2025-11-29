package com.hci.ufosightings.service;

import com.hci.ufosightings.common.Area;
import com.hci.ufosightings.common.User;
import com.hci.ufosightings.dao.AreaDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AreaService {

    private final AreaDao areaDao;

    public List<Area> getAllAreas() {
        return areaDao.findAll();
    }

    public List<User> getAssignedUsers(Long areaId) {
        return areaDao.findById(areaId)
                .map(Area::getAssignedUsers)
                .orElse(Collections.emptyList());
    }
}
