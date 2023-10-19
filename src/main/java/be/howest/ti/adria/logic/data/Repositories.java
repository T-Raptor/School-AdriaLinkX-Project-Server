package be.howest.ti.adria.logic.data;

import be.howest.ti.adria.logic.exceptions.RepositoryException;
import io.vertx.core.json.JsonObject;

public class Repositories {
    private static H2Repository h2Repo = null;

    private Repositories() {
    }

    public static H2Repository getH2Repo() {
        if (h2Repo == null)
            throw new RepositoryException("AdriaH2Repository not configured.");

        return h2Repo;
    }

    public static void configure(JsonObject dbProps) {
        h2Repo = new H2Repository(dbProps.getString("url"),
                dbProps.getString("username"),
                dbProps.getString("password"),
                dbProps.getInteger("webconsole.port"));
    }

    public static void shutdown() {
        if (h2Repo != null)
            h2Repo.cleanUp();

        h2Repo = null;
    }
}
