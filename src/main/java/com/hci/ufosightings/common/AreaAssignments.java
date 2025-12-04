package com.hci.ufosightings.common;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "area_assignments")
public class AreaAssignments {

    @EmbeddedId
    private AreaAssignmentsId id;

    @MapsId("userId")
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @MapsId("areaId")
    @ManyToOne
    @JoinColumn(name = "area_id", nullable = false)
    private Area area;

    @Column(name = "assigned_at", nullable = false)
    private LocalDate assignedAt;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private AssignmentStatus status;

}
