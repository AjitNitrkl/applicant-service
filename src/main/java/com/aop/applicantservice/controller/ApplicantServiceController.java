package com.aop.applicantservice.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApplicantServiceController {
	
	@RequestMapping("getApplicants")
    public String getApplicants(){
        return ("return list of applicants");
    }
	
	@RequestMapping("/")
	String home() {
		return "Hello welcome to Service Api!";
	}

}
