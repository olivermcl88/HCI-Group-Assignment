package com.hci.ufosightings.service;

import com.hci.ufosightings.common.AreaAssignments;
import com.hci.ufosightings.dao.AreaAssignmentsDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AreaAssignmentsService {

    private final AreaAssignmentsDao areaAssignmentsDao;

    public List<AreaAssignments> getAssignmentsByUserId(Long userId) {
        return areaAssignmentsDao.findByUser_UserId(userId);
    }
}

