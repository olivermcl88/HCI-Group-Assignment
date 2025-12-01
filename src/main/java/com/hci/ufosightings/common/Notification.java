package com.hci.ufosightings.common;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "area")
public class Notification {

    @Id
    @Column(name="noti_id", nullable = false, unique = true)
    private Long notiId;

    @Column(name="noti_name", nullable = false, unique = true)
    private String notiName;

    @Column(name="description", nullable = false)
    private String description;
}
