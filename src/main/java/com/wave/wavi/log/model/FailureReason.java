package com.wave.wavi.log.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FailureReason {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FailureType type;

    @Column(nullable = false)
    private String reason;

    public void setId(Long id) {
        this.id = id;
    }

    public void setType(FailureType type) {
        this.type = type;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
