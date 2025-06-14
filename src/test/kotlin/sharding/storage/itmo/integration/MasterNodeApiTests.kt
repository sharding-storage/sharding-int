package sharding.storage.itmo.integration

import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.greaterThanOrEqualTo
import org.hamcrest.Matchers.notNullValue
import org.hamcrest.core.IsEqual.equalTo
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.boot.test.context.SpringBootTest
import sharding.storage.itmo.integration.model.ChangeShardRequest
import sharding.storage.itmo.integration.model.NodeRequest


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
class MasterNodeApiTests {

    private val masterBaseUrl = "http://localhost:9090"

    @BeforeAll
    fun setup() {
        RestAssured.baseURI = masterBaseUrl
    }

    @Test
    fun `health check возвращает OK`() {
        RestAssured
            .get("/health")
            .then()
            .statusCode(200)
            .contentType(ContentType.TEXT)
            .body(equalTo("OK"))
    }

    @Test
    fun `добавление storage-ноды в пул`() {
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
    fun `удаление storage-ноды из пула`() {
        // Сначала добавить, чтобы было что удалять
        val node = NodeRequest(address = "localhost:8086")
        RestAssured
            .given()
            .contentType(ContentType.JSON)
            .body(node)
            .post("/scheme")
            .then()
            .statusCode(200)

        // Теперь удаляем
        RestAssured
            .delete("/scheme/localhost:8086")
            .then()
            .statusCode(200)
            .body("message", containsString("done"))
    }

    @Test
    fun `получение схемы узлов`() {
        RestAssured
            .get("/scheme")
            .then()
            .statusCode(200)
            .body("nodes", notNullValue())
            .body("virtualNodes", greaterThanOrEqualTo(0))
            .body("version", greaterThanOrEqualTo(0))
    }

    @Test
    fun `изменение числа шардов`() {
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