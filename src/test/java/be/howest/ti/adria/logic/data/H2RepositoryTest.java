package be.howest.ti.adria.logic.data;

import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.BeforeEach;

import java.util.Map;

class H2RepositoryTest {
    private static final String URL = "jdbc:h2:./db-12";

    @BeforeEach
    void setupTestSuite() {
        Repositories.shutdown();
        JsonObject dbProperties = new JsonObject(Map.of("url",URL,
                "username", "",
                "password", "",
                "webconsole.port", 9000 ));
        Repositories.configure(dbProperties);
    }
}
