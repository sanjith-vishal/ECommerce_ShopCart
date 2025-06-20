package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "admins")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Admin {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int adminId;

	@NotBlank(message = "Name is required")
	private String name;
	
	@Email(message = "Invalid email format")
	private String email;
	
	@NotBlank(message = "Phone Number cannot be blank")
	private String phoneNumber;

}