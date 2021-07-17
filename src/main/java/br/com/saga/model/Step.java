package br.com.saga.model;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "step")
public class Step extends AbstractEntity {
    @Column(nullable = false)
    private String name;

    @Column(name = "process_router", nullable = false)
    private String processRouter;

    @Column(name = "fallback_router", nullable = false)
    private String fallbackRouter;

    @Column(nullable = false)
    private Long timeout;

    @Column(nullable = false)
    private Integer position;

    @Column(nullable = false)
    @Builder.Default
    private Boolean excluded = false;

    @Column(name = "event_id", insertable = false, updatable = false)
    private Long eventId;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    public Event getEvent() {
        return Hibernate.isInitialized(event) ? event : null;
    }
}
