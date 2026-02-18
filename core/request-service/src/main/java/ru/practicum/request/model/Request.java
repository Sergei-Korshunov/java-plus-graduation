package ru.practicum.request.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.interactionapi.request.model.RequestStatus;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString
@Entity
@Table(name = "request")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime created;

    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "requester_id")
    private Long requesterId;

    @Enumerated(EnumType.STRING)
    private RequestStatus requestStatus;
}