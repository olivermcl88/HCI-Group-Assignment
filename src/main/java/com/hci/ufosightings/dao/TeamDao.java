package com.hci.ufosightings.dao;

import com.hci.ufosightings.common.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamDao extends JpaRepository<Team, Long>{

    Optional<Team> findTeamByTeamId(Long teamId);

    Optional<Team> findByLeaderId(Long leaderId);
}
