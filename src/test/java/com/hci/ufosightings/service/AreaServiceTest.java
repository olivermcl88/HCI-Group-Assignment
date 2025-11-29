package com.hci.ufosightings.service;

import com.hci.ufosightings.common.Area;
import com.hci.ufosightings.common.User;
import com.hci.ufosightings.dao.AreaDao;
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
class AreaServiceTest {

    @Mock
    private AreaDao areaDao;

    @InjectMocks
    private AreaService areaService;

    @Test
    void getAllAreas_returnsListFromDao() {
        Area a1 = Area.builder().areaId(1L).areaName("A1").build();
        Area a2 = Area.builder().areaId(2L).areaName("A2").build();
        List<Area> expected = List.of(a1, a2);

        when(areaDao.findAll()).thenReturn(expected);

        List<Area> actual = areaService.getAllAreas();

        assertEquals(expected, actual);
        verify(areaDao, times(1)).findAll();
    }

    @Test
    void getAssignedUsers_areaExists_returnsAssignedUsers() {
        User user = User.builder().userId(10L).username("bob").build();
        Area area = Area.builder().areaId(5L).assignedUsers(List.of(user)).build();

        when(areaDao.findById(5L)).thenReturn(Optional.of(area));

        List<User> actual = areaService.getAssignedUsers(5L);

        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertEquals(user, actual.getFirst());
        verify(areaDao, times(1)).findById(5L);
    }

    @Test
    void getAssignedUsers_areaDoesNotExist_returnsEmptyList() {
        when(areaDao.findById(99L)).thenReturn(Optional.empty());

        List<User> actual = areaService.getAssignedUsers(99L);

        assertTrue(actual.isEmpty());
        verify(areaDao, times(1)).findById(99L);
    }
}

