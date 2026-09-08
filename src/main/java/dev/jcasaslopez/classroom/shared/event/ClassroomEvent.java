package dev.jcasaslopez.classroom.shared.event;

// Represents the Kafka message payload for Classroom-related events.
// This class is intentionally duplicated across microservices to avoid the overhead of a shared library
// (another exact copy is defined in the Bookings microservice, as this is the consumer of Classroom-related events).
// Consistency is maintained via local mapper.

public record ClassroomEvent(int idClassroom, String name, Integer seats, Boolean projector, Boolean speakers) {}