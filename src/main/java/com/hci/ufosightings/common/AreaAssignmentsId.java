package com.hci.ufosightings.common;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Embeddable
public class AreaAssignmentsId implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "area_id", nullable = false)
    private Long areaId;

    @Column(name = "user_id", nullable = false)
    private Long userId;
}
