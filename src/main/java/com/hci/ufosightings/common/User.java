package com.hci.ufosightings.common;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "user_seq", allocationSize = 1)
    @Column(nullable = false, unique = true, name="user_id")
    private Long userId;

    @Column(name="username", nullable = false, unique = true)
    private String username;

    @Column(name="first_name", nullable = false)
    private String firstName;

    @Column(name="second_name", nullable = false)
    private String secondName;

    @Column(name="email", nullable = false, unique = true)
    private String email;

    @Column(name="role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name="team_id", nullable = false)
    private Long teamId;

    @ManyToMany
    @JoinTable(name = "area_assignments")
    List<Area> assignments;

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId);
    }

}
