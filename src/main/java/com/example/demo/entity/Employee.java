package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "employee")
public class Employee {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name")
	private String name;

	@Column(name = "email")
	private String email;

	

	@Column(name = "role")
	private String role;

	// 🔴 NEW FIELD - SIRF YEH ADD KARNA HAI
	@Column(name = "profile_photo")
	private String profilePhoto;
	
	@Column(name ="password", nullable = true)
	private String password;

	// Constructors
	public Employee() {
	}

	public Employee(String name, String email, String password, String role) {
		this.name = name;
		this.email = email;
		this.password = password;
		this.role = role;
	}

	// Getters and Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	// 🔴 NEW GETTER AND SETTER
	public String getProfilePhoto() {
		return profilePhoto;
	}

	public void setProfilePhoto(String profilePhoto) {
		this.profilePhoto = profilePhoto;
	}

	@Override
	public String toString() {
		return "Employee{" + "id=" + id + ", name='" + name + '\'' + ", email='" + email + '\'' + ", role='" + role
				+ '\'' + ", profilePhoto='" + profilePhoto + '\'' + '}';
	}
}