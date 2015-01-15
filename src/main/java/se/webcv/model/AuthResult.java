package se.webcv.model;

import java.io.Serializable;

public class AuthResult implements Serializable {

	private final String token;
	private final String domain;
	private final String displayName;
	private boolean authenticated;

	public AuthResult(String token, String domain, String displayName, boolean authenticated) {
		this.token = token;
		this.domain = domain;
		this.displayName = displayName;
		this.authenticated = authenticated;
	}

	public String getToken() {
		return token;
	}

	public boolean isAuthenticated() {
		return authenticated;
	}

	public String getDomain() {
		return domain;
	}

	public String getDisplayName() {
		return displayName;
	}
}
