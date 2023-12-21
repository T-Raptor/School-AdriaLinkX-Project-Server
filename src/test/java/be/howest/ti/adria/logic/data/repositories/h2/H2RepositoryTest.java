package be.howest.ti.adria.logic.data.repositories.h2;

import be.howest.ti.adria.logic.data.H2Repository;
import be.howest.ti.adria.logic.data.Repositories;
import be.howest.ti.adria.logic.exceptions.RepositoryException;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class H2RepositoryTest {
    private static final String URL = "jdbc:h2:./db-12";

    private H2Repository repository;


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


    @Test
    void constructorInvalidConfigThrows() {
        // Arrange
        String url = URL;
        String username = "";
        String password = "";
        int console = 9000;

        // Act / Assert
        assertThrows(RepositoryException.class, () -> new H2Repository(url, username, password, console));
    }

    @Test
    void cleanUpIOThrows() throws IOException {
        // Arrange
        Files.delete(Path.of("./db-12.mv.db"));
        Files.createDirectory(Path.of("./db-12.mv.db"));
        Files.createFile(Path.of("./db-12.mv.db/file"));

        // Act
        boolean threw = false;
        try {
            repository.cleanUp();
        } catch (RepositoryException e) {
            threw = true;
        }

        // Cleanup
        Files.delete(Path.of("./db-12.mv.db/file"));
        Files.delete(Path.of("./db-12.mv.db"));

        // Assert
        assertTrue(threw, "cleanUp did not throw RepositoryException");
    }

    @Test
    void generateDataAdditional() {
        // Arrange
        List<String> resources = List.of("goodsql.sql");

        // Act
        boolean result = repository.generateData(resources);

        // Assert
        assertTrue(result, "Data generation failed");
    }

    @Test
    void generateDataBadSqlLogs() {
        // Arrange
        List<String> resources = List.of("badsql.sql");

        // Act
        boolean result = repository.generateData(resources);

        // Assert
        assertFalse(result, "Data generation did not fail");
    }

    @Test
    void generateDataNotFoundThrows() {
        // Arrange
        List<String> resources = List.of("notfound.sql");

        // Act / Assert
        assertThrows(RepositoryException.class, () -> repository.generateData(resources));
    }


    @Test
    void getRowsThrows() {
        // Arrange
        int retValue = 5;

        // Act / Assert
        assertThrows(RepositoryException.class, () -> repository.getRows("select *= from table;", stmt -> { }, rs -> retValue));
    }

    @Test
    void getRowThrows() {
        // Arrange
        int retValue = 5;

        // Act / Assert
        assertThrows(RepositoryException.class, () -> repository.getRow("select * from table where column =;", stmt -> { }, rs -> retValue));
    }

    @Test
    void getNoRow() {
        // Arrange
        int retValue = 5;

        // Act
        Integer actual = repository.getRow("select * from observables where id = -1987452;", stmt -> { }, rs -> retValue);

        // Assert
        assertNull(actual);
    }

    @Test
    void insertRowThrows() {
        // Arrange
        int retValue = 5;

        // Act / Assert
        assertThrows(RepositoryException.class, () -> repository.insertRow("insert into table values (?, ?, ?);", stmt -> { }, rs -> retValue));
    }

    @Test
    void updateRowThrows() {
        // Arrange
        // Act / Assert
        assertThrows(RepositoryException.class, () -> repository.updateRow("update table set column = ? where column =;", stmt -> { }));
    }

    @Test
    void deleteRowThrows() {
        // Arrange
        // Act / Assert
        assertThrows(RepositoryException.class, () -> repository.deleteRow("delete from table where column =;", stmt -> { }));
    }
}
