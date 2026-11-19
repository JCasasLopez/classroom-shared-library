package dev.jcasaslopez.classroom.shared.enums;

import java.time.Duration;

public enum TokenPurpose {

	ACCESS("access", Duration.ofMinutes(15)),
	REFRESH("refresh", Duration.ofDays(7)),
	ACCOUNT_CONFIRMATION("account_confirmation", Duration.ofHours(24)),
	PASSWORD_RESET("password_reset", Duration.ofMinutes(15));

	private final String claimValue;
	private final Duration timeToLive;

	TokenPurpose(String claimValue, Duration timeToLive) {
		this.claimValue = claimValue;
		this.timeToLive = timeToLive;
	}

	public String claimValue() {
		return claimValue;
	}

	public Duration timeToLive() {
		return timeToLive;
	}

}