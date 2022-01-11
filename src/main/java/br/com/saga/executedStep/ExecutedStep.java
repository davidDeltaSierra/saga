package br.com.saga.executedStep;

import br.com.saga.common.entity.AbstractEntity;
import br.com.saga.executedEvent.ExecutedEvent;
import br.com.saga.step.Step;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDateTime;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "executed_step", indexes = {
        @Index(columnList = "uuid"),
        @Index(columnList = "init"),
        @Index(columnList = "end"),
        @Index(columnList = "status"),
})

@NamedEntityGraph(name = ExecutedStep.Graph.executedStepStep, attributeNodes = {
        @NamedAttributeNode(value = "step"),
})
@NamedEntityGraph(name = ExecutedStep.Graph.executedStepStepAndExecutedEvent, attributeNodes = {
        @NamedAttributeNode(value = "step"),
        @NamedAttributeNode(value = "executedEvent"),
})
public class ExecutedStep extends AbstractEntity {
    @Column(nullable = false)
    private String uuid;

    @Column(columnDefinition = "json")
    private String payload;

    @Column(nullable = false)
    private LocalDateTime init;

    @Column
    private LocalDateTime end;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OperationStatus status;

    @Column(name = "step_id", insertable = false, updatable = false)
    private Long stepId;

    @Column(name = "executed_event_id", insertable = false, updatable = false)
    private Long executedEventId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "step_id", nullable = false)
    private Step step;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "executed_event_id", nullable = false)
    private ExecutedEvent executedEvent;

    public Step getStep() {
        return Hibernate.isInitialized(step) ? step : null;
    }

    public ExecutedEvent getExecutedEvent() {
        return Hibernate.isInitialized(executedEvent) ? executedEvent : null;
    }

    public Boolean isExpired() {
        if (isNull(this.getStep()) || isNull(this.init)) {
            return null;
        }
        LocalDateTime expires = this.init.plusSeconds(this.getStep().getTimeout());
        return LocalDateTime.now().isAfter(expires);
    }

    public Boolean isFinalized() {
        return nonNull(end);
    }

    public Boolean isNonFinalized() {
        return !isFinalized();
    }

    public static class Graph {
        public static final String executedStepStep = "executedStepStep";
        public static final String executedStepStepAndExecutedEvent = "executedStepStepAndExecutedEvent";
    }
}
