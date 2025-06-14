package sharding.storage.itmo.integration

import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.junit.jupiter.api.*
import org.hamcrest.Matchers.*
import org.springframework.boot.test.context.SpringBootTest
import sharding.storage.itmo.integration.model.ChangeShardRequest
import sharding.storage.itmo.integration.model.NodeRequest

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
class MasterNodeApiTests {

    private val masterBaseUrl = "http://localhost:9090"

    @BeforeAll
    fun setupRestAssuredBaseUri() {
        RestAssured.baseURI = masterBaseUrl
    }

    @Test
    fun healthCheckReturnsOk() {
        RestAssured
            .get("/health")
            .then()
            .statusCode(200)
            .contentType(ContentType.TEXT)
            .body(equalTo("OK"))
    }

    @Test
    fun addStorageNode() {
        val node = NodeRequest(address = "localhost:8085")
        RestAssured
            .given()
            .contentType(ContentType.JSON)
            .body(node)
            .post("/scheme")
            .then()
            .statusCode(200)
            .body("message", containsString("done"))
    }

    @Test
    fun removeStorageNode() {
        // Ensure node exists before trying to remove
        val node = NodeRequest(address = "localhost:8086")
        RestAssured
            .given()
            .contentType(ContentType.JSON)
            .body(node)
            .post("/scheme")
            .then()
            .statusCode(200)

        RestAssured
            .delete("/scheme/localhost:8086")
            .then()
            .statusCode(200)
            .body("message", containsString("done"))
    }

    @Test
    fun getClusterScheme() {
        RestAssured
            .get("/scheme")
            .then()
            .statusCode(200)
            .body("nodes", notNullValue())
            .body("virtualNodes", greaterThanOrEqualTo(0))
            .body("version", greaterThanOrEqualTo(0))
    }

    @Test
    fun updateShardCount() {
        val req = ChangeShardRequest(shardCount = 4)
        RestAssured
            .given()
            .contentType(ContentType.JSON)
            .body(req)
            .put("/shards")
            .then()
            .statusCode(200)
            .body("message", containsString("done"))
    }
}