package com.hci.ufosightings.service;

import com.hci.ufosightings.common.User;
import com.hci.ufosightings.dao.UserDao;
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
class UserServiceTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserService userService;

    @Test
    void getAllUsers_returnsListFromDao() {
        User u1 = User.builder().userId(1L).username("alice").build();
        User u2 = User.builder().userId(2L).username("bob").build();
        List<User> expected = List.of(u1, u2);

        when(userDao.findAll()).thenReturn(expected);

        List<User> actual = userService.getAllUsers();

        assertEquals(expected, actual);
        verify(userDao, times(1)).findAll();
    }

    @Test
    void getUserById_found_returnsUser() {
        User u = User.builder().userId(10L).username("sam").build();
        when(userDao.findById(10L)).thenReturn(Optional.of(u));

        User actual = userService.getUserById(10L);

        assertNotNull(actual);
        assertEquals(u, actual);
        verify(userDao, times(1)).findById(10L);
    }

    @Test
    void getUserById_notFound_returnsNull() {
        when(userDao.findById(123L)).thenReturn(Optional.empty());

        User actual = userService.getUserById(123L);

        assertNull(actual);
        verify(userDao, times(1)).findById(123L);
    }

    @Test
    void getUserByUsername_found_returnsUser() {
        User u = User.builder().userId(4L).username("dave").build();
        when(userDao.findByUsername("dave")).thenReturn(Optional.of(u));

        User actual = userService.getUserByUsername("dave");

        assertNotNull(actual);
        assertEquals(u, actual);
        verify(userDao, times(1)).findByUsername("dave");
    }

    @Test
    void getUserByUsername_notFound_returnsNull() {
        when(userDao.findByUsername("ghost")).thenReturn(Optional.empty());

        User actual = userService.getUserByUsername("ghost");

        assertNull(actual);
        verify(userDao, times(1)).findByUsername("ghost");
    }
}

