package dev.jcasaslopez.classroom.shared.enums;

public enum TokenType {
	VERIFICATION("verification"),
	ACCESS("access"),
	REFRESH("refresh");

	private final String prefix;

	TokenType(String prefix) {
		this.prefix = prefix;
	}

	public String prefix() {
		return prefix;
	}
}