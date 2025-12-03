package com.hci.ufosightings.common;


import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @Column(name="noti_id", nullable = false, unique = true)
    private Long notiId;

    @Column(name="user_id", nullable = false)
    private Long userId;

    @Column(name="title", nullable = false)
    private String title;

    @Column(name="message", nullable = false)
    private String message;
}
