package dev.jcasaslopez.classroom.shared.enums;

public enum TokenPurpose {
	VERIFICATION("verification"),
	ACCESS("access"),
	REFRESH("refresh");

	private final String prefix;

	TokenPurpose(String prefix) {
		this.prefix = prefix;
	}

	public String prefix() {
		return prefix;
	}
}