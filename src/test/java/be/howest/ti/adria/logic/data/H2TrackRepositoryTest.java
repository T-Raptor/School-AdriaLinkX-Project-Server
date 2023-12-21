package be.howest.ti.adria.logic.data;

import be.howest.ti.adria.logic.data.repositories.TrackRepositoryTest;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.BeforeEach;

import java.util.Map;

class H2TrackRepositoryTest extends TrackRepositoryTest {
    private static final String URL = "jdbc:h2:./db-12";


    @BeforeEach
    void setupTestSuite() {
        Repositories.shutdown();
        JsonObject dbProperties = new JsonObject(Map.of("url",URL,
                "username", "",
                "password", "",
                "webconsole.port", 9000 ));
        Repositories.configure(dbProperties);
        this.stationRepository = Repositories.getH2Repo();
        this.repository = Repositories.getH2Repo();
    }
}
