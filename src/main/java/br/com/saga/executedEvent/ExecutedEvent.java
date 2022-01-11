package br.com.saga.executedEvent;

import br.com.saga.common.entity.AbstractEntity;
import br.com.saga.event.Event;
import br.com.saga.executedStep.ExecutedStep;
import br.com.saga.executedStep.OperationStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "executed_event", indexes = {
        @Index(columnList = "uuid"),
        @Index(columnList = "init"),
        @Index(columnList = "end"),
        @Index(columnList = "status"),
})

@NamedEntityGraph(name = ExecutedEvent.Graph.executedEventEventAndExecutedSteps, attributeNodes = {
        @NamedAttributeNode("event"),
        @NamedAttributeNode("executedSteps"),
})
@NamedEntityGraph(name = ExecutedEvent.Graph.executedEventEventAndExecutedStepsAndExecutedSteps_Step, attributeNodes = {
        @NamedAttributeNode("event"),
        @NamedAttributeNode(value = "executedSteps", subgraph = "executedSteps.step"),
}, subgraphs = {
        @NamedSubgraph(name = "executedSteps.step", attributeNodes = {
                @NamedAttributeNode(value = "step"),
        })
})
public class ExecutedEvent extends AbstractEntity {
    @Column(nullable = false)
    private String uuid;

    @Column(nullable = false)
    private LocalDateTime init;

    @Column
    private LocalDateTime end;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OperationStatus status;

    @Column(name = "event_id", insertable = false, updatable = false)
    private Long eventId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @JsonIgnoreProperties("executedEvent")
    @OneToMany(mappedBy = "executedEvent", fetch = FetchType.LAZY)
    private Set<ExecutedStep> executedSteps;

    public Event getEvent() {
        return Hibernate.isInitialized(event) ? event : null;
    }

    public Set<ExecutedStep> getExecutedSteps() {
        return Hibernate.isInitialized(executedSteps) ? executedSteps : null;
    }

    public static class Graph {
        public static final String executedEventEventAndExecutedSteps = "executedEventEventAndExecutedSteps";
        public static final String executedEventEventAndExecutedStepsAndExecutedSteps_Step = "executedEventEventAndExecutedStepsAndExecutedSteps_Step";
    }
}
