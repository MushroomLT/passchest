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
			this.username = "";
		} else {
			this.email = "";
			this.username = user;
		}
		this.password = password;
	}
	
	@Override
	public String toString() {
		String hiddenValue = "";
		for(int i = 0; i < this.password.length(); i++) {
			hiddenValue += "*";
		}
		return "User: " + username + ". Email: " + email + ". Password: " + hiddenValue;
	}
}
