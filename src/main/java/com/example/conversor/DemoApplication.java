package com.example.conversor;

import com.example.conversor.servico.ProcessarTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class DemoApplication {
	public static void main(String[] args) throws IOException {
		SpringApplication.run(DemoApplication.class, args);
	}

}
