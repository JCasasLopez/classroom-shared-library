package dev.jcasaslopez.classroom.shared.event;

// Represents the Kafka message payload for Classroom-related events.
// This class is intentionally duplicated across microservices to avoid the overhead of a shared library
// (another exact copy is defined in the Bookings microservice, as this is the consumer of Classroom-related events).
// Consistency is maintained via local mapper.

public class ClassroomEvent {
	private int idClassroom;
	private String name;
	private Integer seats;
	private Boolean projector;
	private Boolean speakers;
	
	public ClassroomEvent(int idClassroom, String name, Integer seats, Boolean projector, Boolean speakers) {
		this.idClassroom = idClassroom;
		this.name = name;
		this.seats = seats;
		this.projector = projector;
		this.speakers = speakers;
	}

	public ClassroomEvent() {
	}

	public int getIdClassroom() {
		return idClassroom;
	}

	public void setIdClassroom(int idClassroom) {
		this.idClassroom = idClassroom;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getSeats() {
		return seats;
	}

	public void setSeats(Integer seats) {
		this.seats = seats;
	}

	public Boolean getProjector() {
		return projector;
	}

	public void setProjector(Boolean projector) {
		this.projector = projector;
	}

	public Boolean getSpeakers() {
		return speakers;
	}

	public void setSpeakers(Boolean speakers) {
		this.speakers = speakers;
	}
}
