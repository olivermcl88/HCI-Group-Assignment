package com.hci.ufosightings.service;

import com.hci.ufosightings.common.Team;
import com.hci.ufosightings.dao.TeamDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TeamService {

    private final TeamDao teamDao;

    public List<Team> getAllTeams() {
        return teamDao.findAll();
    }

    public Team getTeamById(Long id) {
        return teamDao.findById(id).orElse(null);
    }

    public Team getTeamByLeaderId(Long leaderId) {
        return teamDao.findByLeaderId(leaderId).orElse(null);
    }
}
