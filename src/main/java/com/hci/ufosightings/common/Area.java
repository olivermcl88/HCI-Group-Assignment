package com.hci.ufosightings.common;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "area")
public class Area {

    @Id
    @Column(name="area_id", nullable = false, unique = true)
    private Long areaId;

    @Column(name="area_name", nullable = false, unique = true)
    private String areaName;

    @Column(name="description", nullable = false)
    private String description;

    @Column(name="priority", nullable = false)
    @Enumerated(EnumType.STRING)
    private AreaPriority areaPriority;

    @OneToMany(mappedBy = "area")
    private List<AreaAssignments> assignments;

    public List<User> getAssignedUsers() {
        if (assignments == null) return List.of();
        return assignments.stream().map(AreaAssignments::getUser).collect(Collectors.toList());
    }
}
