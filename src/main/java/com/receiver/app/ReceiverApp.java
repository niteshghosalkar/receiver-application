package com.receiver.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.receiver.app.service.ReceiverService;

@SpringBootApplication
public class ReceiverApp implements CommandLineRunner {

	@Autowired
	ReceiverService recieverService;

	public static void main(String[] args) {
		SpringApplication springApplication = new SpringApplication(ReceiverApp.class);
		springApplication.setWebApplicationType(WebApplicationType.NONE);
		springApplication.run(args);

	}

	public void run(String... arg) throws Exception {
		recieverService.process();
	}

}
