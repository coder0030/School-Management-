package com.example.demo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
}


//1️⃣ Login API → token generated
//2️⃣ Client stores token
//
//3️⃣ Client sends request:
//   Authorization: Bearer <token>
//
//4️⃣ JwtFilter runs
//   - Read header
//   - Remove Bearer
//   - Extract email
//   - Load user from DB
//   - Validate token
//   - Set authentication
//
//5️⃣ Controller executes

