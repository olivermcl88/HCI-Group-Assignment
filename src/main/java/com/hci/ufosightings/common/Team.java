package com.hci.ufosightings.common;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "team")
public class Team {

    @Id
    @Column(name = "team_id", nullable = false, unique = true)
    private Long teamId;

    @Column(name = "team_name",unique = true)
    private String teamName;

    @Column(name = "leader_id")
    private Long leaderId;
}
