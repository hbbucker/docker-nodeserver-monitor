package br.com.bucker.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class DockerInfoResourceTest {

    @Test
    public void testStatusEndpoint() {
        given()
          .when().get("/docker/status")
          .then()
             .statusCode(200);
    }

}