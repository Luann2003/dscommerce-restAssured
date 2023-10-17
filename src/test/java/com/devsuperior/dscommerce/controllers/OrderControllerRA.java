package com.devsuperior.dscommerce.controllers;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;

import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.devsuperior.dscommerce.tests.TokenUtil;

import io.restassured.http.ContentType;

public class OrderControllerRA {
	
	private String clientUsername, clientPassword, adminUsername, adminPassword;
	private String adminToken, clientToken, invalidToken;
	
	private Long existingId, nonExistingId;
	
	@BeforeEach
	void setup() throws JSONException {
		baseURI = "http://localhost:8080";
		
		clientUsername = "maria@gmail.com";
		clientPassword = "123456";
		adminUsername = "alex@gmail.com";
		adminPassword = "123456";
		
		clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);
		adminToken = TokenUtil.obtainAccessToken(adminUsername, adminPassword);
		invalidToken = adminToken + "xpto";
		
	}
	
	@Test
	public void findByIdReturnOrderExistingWhenLoggedAdmin() {
		existingId = 1L;
		
		given()
			.header("Content.type", "application/json")
			.header("Authorization", "Bearer " + adminToken)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.get("/orders/{id}", existingId)
		.then()
			.statusCode(200)
			.body("id", is(1))
			.body("moment", equalTo("2022-07-25T13:00:00Z"))
			.body("status", equalTo("PAID"))
			.body("client.name", equalTo("Maria Brown"))
			.body("payment.moment", equalTo("2022-07-25T15:00:00Z"))
			.body("items.name", hasItems("The Lord of the Rings", "Macbook Pro"))
			.body("total", is(1431.0F));
	}
	
	@Test
	public void findByIdShouldReturnOrderDTOWhenIdExistsAndClientLogged() {
		existingId = 1L;
		
		given()
			.header("Content.type", "application/json")
			.header("Authorization", "Bearer " + clientToken)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.get("/orders/{id}", existingId)
		.then()
			.statusCode(200)
			.body("id", is(1))
			.body("moment", equalTo("2022-07-25T13:00:00Z"))
			.body("status", equalTo("PAID"))
			.body("client.name", equalTo("Maria Brown"))
			.body("payment.moment", equalTo("2022-07-25T15:00:00Z"))
			.body("items.name", hasItems("The Lord of the Rings", "Macbook Pro"))
			.body("total", is(1431.0F));
	}
	
	@Test
	public void findByIdShouldReturnForbiddenWhenIdExistsAndClientLoggedAndOrderDoesNotBelongUser() {
		Long otherId = 2L;
		
		given()
			.header("Content.type", "application/json")
			.header("Authorization", "Bearer " + clientToken)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.get("/orders/{id}", otherId)
		.then()
			.statusCode(403)
			.body("error", equalTo("Access denied. Should be self or admin"));
	}
	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExistAndAdminLogged() throws Exception {
		
		nonExistingId = 1000L;
		
		given()
			.header("Content.type", "application/json")
			.header("Authorization", "Bearer " + adminToken)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.get("/orders/{id}", nonExistingId)
		.then()
			.statusCode(404);
	}
	
	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExistAndClientLogged() {
		
		nonExistingId = 1000L;
		
		given()
			.header("Content.type", "application/json")
			.header("Authorization", "Bearer " + clientToken)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.get("/orders/{id}", nonExistingId)
		.then()
			.statusCode(404);
	}
	
	@Test
	public void findByIdShouldReturnUnauthorizedWhenIdExistsAndInvalidToken() {
		existingId = 1L;
		
		given()
			.header("Content.type", "application/json")
			.header("Authorization", "Bearer " + invalidToken)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.get("/orders/{id}", existingId)
		.then()
			.statusCode(401);
	}
	

}
