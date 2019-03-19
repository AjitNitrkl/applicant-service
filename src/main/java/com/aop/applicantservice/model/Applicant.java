package com.aop.applicantservice.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class Applicant {
	
	private String id;
	private String fname;
	private String lname;
	private String email;
	private int age;

}
