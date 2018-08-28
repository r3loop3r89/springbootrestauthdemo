package com.shra1.javamster.dto;

import java.io.Serializable;

public class GenericJsonResponse implements Serializable {

	private static final long serialVersionUID = -3216385258186503183L;

	private LoginDomainSpecificStatus domainSpecificStatus;
	private String message;

	public GenericJsonResponse(LoginDomainSpecificStatus domainSpecificStatus, String message) {
		this.domainSpecificStatus = domainSpecificStatus;
		this.message = message;
	}

	public LoginDomainSpecificStatus getDomainSpecificStatus() {
		return domainSpecificStatus;
	}

	public void setDomainSpecificStatus(LoginDomainSpecificStatus domainSpecificStatus) {
		this.domainSpecificStatus = domainSpecificStatus;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
