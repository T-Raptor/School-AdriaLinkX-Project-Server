package be.howest.ti.adria.logic.data.repositories.h2;

import be.howest.ti.adria.logic.data.Repositories;
import be.howest.ti.adria.logic.data.repositories.NotificationRepositoryTest;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.BeforeEach;

import java.util.Map;

class H2NotificationRepositoryTest extends NotificationRepositoryTest {
    private static final String URL = "jdbc:h2:./db-12";


    @BeforeEach
    void setupTestSuite() {
        Repositories.shutdown();
        JsonObject dbProperties = new JsonObject(Map.of("url",URL,
                "username", "",
                "password", "",
                "webconsole.port", 9000 ));
        Repositories.configure(dbProperties);
        this.repository = Repositories.getH2Repo();
    }
}
