package com.hci.ufosightings.common;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

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

    @Column(name="assigned_at", nullable = false)
    private LocalDate assignedAt;

    @Column(name="status", nullable = false)
    @Enumerated(EnumType.STRING)
    private AssignmentStatus status;

}
