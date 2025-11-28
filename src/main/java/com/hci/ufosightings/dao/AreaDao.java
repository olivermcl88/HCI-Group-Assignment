package com.hci.ufosightings.dao;

import com.hci.ufosightings.common.Area;
import com.hci.ufosightings.common.AreaPriority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AreaDao extends JpaRepository<Area, Long> {

    Optional<Area> findByAreaId(Long areaId);

    Optional<Area> findByAreaName(String name);

    List<Area> findByAreaPriority(AreaPriority areaPriority);

}
