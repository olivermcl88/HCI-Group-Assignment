package com.hci.ufosightings.service;

import com.hci.ufosightings.common.Team;
import com.hci.ufosightings.dao.TeamDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock
    private TeamDao teamDao;

    @InjectMocks
    private TeamService teamService;

    @Test
    void getAllTeams_returnsListFromDao() {
        Team t1 = Team.builder().teamId(1L).teamName("Alpha").build();
        Team t2 = Team.builder().teamId(2L).teamName("Beta").build();
        List<Team> expected = List.of(t1, t2);

        when(teamDao.findAll()).thenReturn(expected);

        List<Team> actual = teamService.getAllTeams();

        assertEquals(expected, actual);
        verify(teamDao, times(1)).findAll();
    }

    @Test
    void getTeamById_found_returnsTeam() {
        Team t = Team.builder().teamId(5L).teamName("X").build();
        when(teamDao.findById(5L)).thenReturn(Optional.of(t));

        Team actual = teamService.getTeamById(5L);

        assertNotNull(actual);
        assertEquals(t, actual);
        verify(teamDao, times(1)).findById(5L);
    }

    @Test
    void getTeamById_notFound_returnsNull() {
        when(teamDao.findById(77L)).thenReturn(Optional.empty());

        Team actual = teamService.getTeamById(77L);

        assertNull(actual);
        verify(teamDao, times(1)).findById(77L);
    }

    @Test
    void getTeamByLeaderId_found_returnsTeam() {
        Team t = Team.builder().teamId(8L).leaderId(2L).teamName("Lead").build();
        when(teamDao.findByLeaderId(2L)).thenReturn(Optional.of(t));

        Team actual = teamService.getTeamByLeaderId(2L);

        assertNotNull(actual);
        assertEquals(t, actual);
        verify(teamDao, times(1)).findByLeaderId(2L);
    }

    @Test
    void getTeamByLeaderId_notFound_returnsNull() {
        when(teamDao.findByLeaderId(999L)).thenReturn(Optional.empty());

        Team actual = teamService.getTeamByLeaderId(999L);

        assertNull(actual);
        verify(teamDao, times(1)).findByLeaderId(999L);
    }
}

