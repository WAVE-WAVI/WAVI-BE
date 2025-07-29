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
@Table(name = "habit_failure_log", uniqueConstraints = {
        @UniqueConstraint(
                name = "HABITLOG_FAILUREREASON_UNIQUE",
                columnNames = {"habit_log_id", "reason_id"}
        )
})
public class HabitFailureLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "habitLogId")
    private HabitLog habitLog;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reasonId")
    private FailureReason reason;
}
