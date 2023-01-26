package com.generation.blogpessoal.model;

public class ResponseLogin {
	
	private Long id;
	private String token;

	public ResponseLogin(Long id, String token) {
		this.id = id;
		this.token = token;
	}
	
    public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getToken() {
		return this.token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
}