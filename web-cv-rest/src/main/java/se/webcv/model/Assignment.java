package se.webcv.model;

import org.springframework.data.annotation.Id;

public class Assignment {

	@Id
	private String id;
	private String customer;
	private String role;
	private String techniques;
	private String description;
	private String date;
	
	public String getCustomer() {
		return customer;
	}
	
	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getTechniques() {
		return techniques;
	}

	public void setTechniques(String techniques) {
		this.techniques = techniques;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	
	
}
