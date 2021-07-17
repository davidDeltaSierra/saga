package br.com.saga.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "event")

@NamedEntityGraph(name = Event.Graph.eventSteps, attributeNodes = {
        @NamedAttributeNode(value = "steps"),
})
public class Event extends AbstractEntity {
    @Column(nullable = false)
    private String name;

    @JsonIgnoreProperties("event")
    @OrderBy("position ASC")
    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY)
    private Set<Step> steps = new HashSet<>();

    public Set<Step> getSteps() {
        return Hibernate.isInitialized(steps) ? steps : null;
    }

    public static class Graph {
        public static final String eventSteps = "eventSteps";
    }
}
