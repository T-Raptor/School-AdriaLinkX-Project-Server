package be.howest.ti.adria.logic.data;

import be.howest.ti.adria.logic.domain.Quote;
import be.howest.ti.adria.logic.domain.Station;
import be.howest.ti.adria.logic.exceptions.RepositoryException;
import org.h2.tools.Server;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
This is only a starter class to use an H2 database.
In this start project there was no need for a Java interface AdriaRepository.
Please always use interfaces when needed.

To make this class useful, please complete it with the topics seen in the module OOA & SD
 */

public class H2Repository implements StationRepository {
    private static final Logger LOGGER = Logger.getLogger(H2Repository.class.getName());
    private static final String SQL_QUOTA_BY_ID = "select id, quote from quotes where id = ?;";
    private static final String SQL_INSERT_QUOTE = "insert into quotes (`quote`) values (?);";
    private static final String SQL_UPDATE_QUOTE = "update quotes set quote = ? where id = ?;";
    private static final String SQL_DELETE_QUOTE = "delete from quotes where id = ?;";

    private static final String SQL_SELECT_STATIONS = "select observable_id, name, latitude, longitude from stations;";
    private static final String SQL_SELECT_STATION = "select observable_id, name, latitude, longitude from stations where id = ?;";
    private static final String SQL_INSERT_STATION = "insert into stations values (?, ?, ?, ?);";
    private static final String SQL_UPDATE_STATION = "update stations set  = ? where observable_id = ?;";
    private static final String SQL_DELETE_STATION = "delete from stations where observable_id = ?;";

    private final Server dbWebConsole;
    private final String username;
    private final String password;
    private final String url;

    public H2Repository(String url, String username, String password, int console) {
        try {
            this.username = username;
            this.password = password;
            this.url = url;
            this.dbWebConsole = Server.createWebServer(
                    "-ifNotExists",
                    "-webPort", String.valueOf(console)).start();
            LOGGER.log(Level.INFO, "Database web console started on port: {0}", console);
            this.generateData();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "DB configuration failed", ex);
            throw new RepositoryException("Could not configure AdriaH2repository");
        }
    }


    private void executeScript(String fileName) throws IOException, SQLException {
        String createDbSql = readFile(fileName);
        try (
                Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(createDbSql);
        ) {
            stmt.executeUpdate();
        }
    }

    private String readFile(String fileName) throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
        if (inputStream == null)
            throw new RepositoryException("Could not read file: " + fileName);

        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }


    public void cleanUp() {
        if (dbWebConsole != null && dbWebConsole.isRunning(false))
            dbWebConsole.stop();

        try {
            Files.deleteIfExists(Path.of("./db-12.mv.db"));
            Files.deleteIfExists(Path.of("./db-12.trace.db"));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Database cleanup failed.", e);
            throw new RepositoryException("Database cleanup failed.");
        }
    }

    public void generateData() {
        try {
            executeScript("db-create.sql");
            executeScript("db-populate.sql");
        } catch (IOException | SQLException ex) {
            LOGGER.log(Level.SEVERE, "Execution of database scripts failed.", ex);
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }


    private <T> T getRow(String statement, SqlConsumer<PreparedStatement> configurator, SqlFunction<ResultSet,T> processor) {
        try (
                Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(statement)
        ) {
            configurator.accept(stmt);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return processor.apply(rs);
                } else {
                    return null;
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to get rows.", ex);
            throw new RepositoryException("Could not get rows.");
        }
    }

    private <T> List<T> getRows(String statement, SqlConsumer<PreparedStatement> configurator, SqlFunction<ResultSet,T> processor) {
        try (
                Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(statement)
        ) {
            configurator.accept(stmt);
            try (ResultSet rs = stmt.executeQuery()) {
                List<T> rows = new ArrayList<>();
                while (rs.next()) {
                    rows.add(processor.apply(rs));
                }
                return rows;
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to get rows.", ex);
            throw new RepositoryException("Could not get rows.");
        }
    }

    private <T> T insertRow(String statement, SqlConsumer<PreparedStatement> configurator, SqlFunction<ResultSet,T> processor) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {

            configurator.accept(stmt);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating row failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return processor.apply(generatedKeys);
                }
                else {
                    throw new SQLException("Creating row failed, no ID obtained.");
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to create row.", ex);
            throw new RepositoryException("Could not create row.");
        }
    }

    private void updateRow(String statement, SqlConsumer<PreparedStatement> configurator) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(statement)) {

            configurator.accept(stmt);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to update row.", ex);
            throw new RepositoryException("Could not update row.");
        }
    }

    public void deleteRow(String statement, SqlConsumer<PreparedStatement> configurator) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(statement)) {

            configurator.accept(stmt);
            stmt.execute();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to delete row.", ex);
            throw new RepositoryException("Could not delete row.");
        }
    }


    public Quote getQuote(int id) {
        return getRow(
                SQL_QUOTA_BY_ID,
                stmt -> stmt.setInt(1, id),
                rs -> new Quote(rs.getInt("id"), rs.getString("quote"))
        );
    }

    public Quote insertQuote(String quoteValue) {
        return insertRow(
                SQL_INSERT_QUOTE,
                stmt -> stmt.setString(1, quoteValue),
                rs -> new Quote(rs.getInt(1), quoteValue)
        );
    }

    public Quote updateQuote(int quoteId, String quote) {
        updateRow(
                SQL_UPDATE_QUOTE,
                stmt -> {
                    stmt.setString(1, quote);
                    stmt.setInt(2, quoteId);
                }
        );
        return new Quote(quoteId, quote);
    }

    public void deleteQuote(int quoteId) {
        deleteRow(
                SQL_DELETE_QUOTE,
                stmt -> stmt.setInt(1, quoteId)
        );
    }


    @Override
    public List<Station> getStations() {
        return null;
    }

    @Override
    public Station getStation(int id) {
        return null;
    }

    @Override
    public Station insertStation(String name, double latitude, double longitude) {
        return null;
    }

    @Override
    public Station updateStation(int id, String name, double latitude, double longitude) {
        return null;
    }

    @Override
    public Station deleteStation(int id) {
        return null;
    }

}
