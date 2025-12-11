package com.sopa.web_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebApiApplication implements org.springframework.boot.CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(WebApiApplication.class, args);
	}

	@org.springframework.beans.factory.annotation.Autowired
	private com.sopa.web_api.server.GameServer gameServer;

	@Override
	public void run(String... args) throws Exception {
		gameServer.start();
	}
}
