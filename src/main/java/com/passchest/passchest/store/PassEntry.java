package com.passchest.passchest.store;

public class PassEntry {
	public String username;
	public String email;
	public String password;
	
	public PassEntry(){
		
	}
	
	public PassEntry(String username, String email, String password) {
		this.username = username;
		this.email = email;
		this.password = password;
	}
	
	public PassEntry(String user, String password) {
		if(user.contains("@")) {
			this.email = user;
		} else {
			this.username = user;
		}
		this.password = password;
	}
}
