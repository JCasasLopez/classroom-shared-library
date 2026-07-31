package dev.jcasaslopez.classroom.shared.domain;

// This class contains the user information that will be extracted from the JWT during the authentication process
// and set into UserContext.
public class UserInfo {
	
	private String email;
	private Integer idUser;
	
	public UserInfo(String email, Integer idUser) {
		this.email = email;
		this.idUser = idUser;
	}

	public String getEmail() {
		return email;
	}

	public Integer getIdUser() {
		return idUser;
	}

}