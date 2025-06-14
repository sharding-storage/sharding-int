package sharding.storage.itmo.integration

import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.junit.jupiter.api.*
import org.hamcrest.Matchers.*
import org.springframework.boot.test.context.SpringBootTest

data class NodeRequest(val address: String)
data class ChangeShardRequest(val shardCount: Int)

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
    fun addStorageNodeActuallyUpdatesScheme() {
        val address = "localhost:8085"
        val node = NodeRequest(address = address)
        // Добавляем ноду
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(node)
            .post("/scheme")
            .then()
            .statusCode(200)

        // Проверяем, что она появилась в схеме
        RestAssured.get("/scheme")
            .then()
            .statusCode(200)
            .body("nodes", hasItem(address))
    }

    @Test
    fun removeStorageNodeActuallyUpdatesScheme() {
        val address = "localhost:8086"
        val node = NodeRequest(address = address)
        // Добавляем, чтобы гарантировать её наличие
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(node)
            .post("/scheme")
            .then()
            .statusCode(200)

        // Удаляем
        RestAssured.delete("/scheme/$address")
            .then()
            .statusCode(200)

        // Проверяем, что её нет в схеме
        RestAssured.get("/scheme")
            .then()
            .statusCode(200)
            .body("nodes", not(hasItem(address)))
    }

    @Test
    fun getClusterSchemeReturnsValidScheme() {
        RestAssured
            .get("/scheme")
            .then()
            .statusCode(200)
            .body("nodes", notNullValue())
            .body("virtualNodes", greaterThanOrEqualTo(0))
            .body("version", greaterThanOrEqualTo(0))
    }

    @Test
    fun updateShardCountActuallyUpdatesScheme() {
        // Получаем текущее число шардов
        val oldVirtualNodes = RestAssured.get("/scheme")
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getInt("virtualNodes")

        // Меняем число шардов (например, увеличиваем на 1)
        val newShardCount = oldVirtualNodes + 1
        val req = ChangeShardRequest(shardCount = newShardCount)
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(req)
            .put("/shards")
            .then()
            .statusCode(200)

        // Проверяем, что число виртуальных нод обновилось
        RestAssured.get("/scheme")
            .then()
            .statusCode(200)
            .body("virtualNodes", equalTo(newShardCount))
    }
}