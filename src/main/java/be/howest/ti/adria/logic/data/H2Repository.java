package be.howest.ti.adria.logic.data;

import be.howest.ti.adria.logic.domain.Quote;
import be.howest.ti.adria.logic.domain.Reservation;
import be.howest.ti.adria.logic.domain.Station;
import be.howest.ti.adria.logic.domain.Track;
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

public class H2Repository implements StationRepository, TrackRepository, ReservationRepository {
    private static final Logger LOGGER = Logger.getLogger(H2Repository.class.getName());
    private static final String SQL_QUOTA_BY_ID = "select id, quote from quotes where id = ?;";
    private static final String SQL_INSERT_QUOTE = "insert into quotes (`quote`) values (?);";
    private static final String SQL_UPDATE_QUOTE = "update quotes set quote = ? where id = ?;";
    private static final String SQL_DELETE_QUOTE = "delete from quotes where id = ?;";

    private static final String SQL_INSERT_OBSERVABLE = "insert into observables values ();";

    private static final String SQL_SELECT_STATIONS = "select observable_id as id, name, latitude, longitude from stations;";
    private static final String SQL_SELECT_STATION = "select observable_id as id, name, latitude, longitude from stations where observable_id = ?;";
    private static final String SQL_INSERT_STATION = "insert into stations values (?, ?, ?, ?);";
    private static final String SQL_UPDATE_STATION = "update stations set `name` = ?, `latitude` = ?, `longitude` = ? where observable_id = ?;";
    private static final String SQL_DELETE_STATION = "delete from stations where observable_id = ?;";

    private static final String SQL_SELECT_TRACKS = "select t.observable_id as id, s1.observable_id as s1_id, s1.name as s1_name, s1.latitude as s1_latitude, s1.longitude as s1_longitude, s2.observable_id as s2_id, s2.name as s2_name, s2.latitude as s2_latitude, s2.longitude as s2_longitude from tracks as t join stations as s1 on station1 = s1.observable_id join stations as s2 on station2 = s2.observable_id;";
    private static final String SQL_SELECT_TRACK = "select t.observable_id as id, s1.observable_id as s1_id, s1.name as s1_name, s1.latitude as s1_latitude, s1.longitude as s1_longitude, s2.observable_id as s2_id, s2.name as s2_name, s2.latitude as s2_latitude, s2.longitude as s2_longitude from tracks as t join stations as s1 on station1 = s1.observable_id join stations as s2 on station2 = s2.observable_id where t.observable_id = ?;";
    private static final String SQL_INSERT_TRACK = "insert into tracks values (?, ?, ?);";
    private static final String SQL_UPDATE_TRACK = "update tracks set `station1` = ?, `station2` = ? where observable_id = ?;";
    private static final String SQL_DELETE_TRACK = "delete from tracks where observable_id = ?;";

    private static final String SQL_SELECT_RESERVATIONS = "select observable_id as id, period_start, period_stop, company from reservations;";
    private static final String SQL_SELECT_RESERVATION_TRACKS = "select reservation, track from reservation_tracks where reservation = ?;";
    private static final String SQL_SELECT_RESERVATION = "select observable_id as id, period_start, period_stop, company from reservations where observable_id = ?;";
    private static final String SQL_INSERT_RESERVATION = "insert into reservations values (?, ?, ?, ?);";
    private static final String SQL_INSERT_RESERVATION_TRACK = "insert into reservation_tracks values (?, ?);";
    private static final String SQL_DELETE_RESERVATION = "delete from reservations where observable_id = ?;";
    private static final String SQL_DELETE_RESERVATION_TRACKS = "delete from reservation_tracks where reservation = ?;";


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


    private int insertObservable() {
        return insertRow(
                SQL_INSERT_OBSERVABLE,
                stmt -> {},
                rs -> rs.getInt(1)
        );
    }


    @Override
    public List<Station> getStations() {
        return getRows(
                SQL_SELECT_STATIONS,
                stmt -> { },
                rs -> new Station(rs.getInt("id"), rs.getString("name"), rs.getDouble("latitude"), rs.getDouble("longitude"))
        );
    }

    @Override
    public Station getStation(int id) {
        return getRow(
                SQL_SELECT_STATION,
                stmt -> stmt.setInt(1, id),
                rs -> new Station(rs.getInt("id"), rs.getString("name"), rs.getDouble("latitude"), rs.getDouble("longitude"))
        );
    }

    @Override
    public Station insertStation(String name, double latitude, double longitude) {
        int id = insertObservable();
        return insertRow(
                SQL_INSERT_STATION,
                stmt -> {
                    stmt.setInt(1, id);
                    stmt.setString(2, name);
                    stmt.setDouble(3, latitude);
                    stmt.setDouble(4, longitude);
                },
                rs -> new Station(id, name, latitude, longitude)
        );
    }

    @Override
    public Station updateStation(int id, String name, double latitude, double longitude) {
        updateRow(
                SQL_UPDATE_STATION,
                stmt -> {
                    stmt.setString(1, name);
                    stmt.setDouble(2, latitude);
                    stmt.setDouble(3, longitude);
                    stmt.setInt(4, id);
                }
        );
        return new Station(id, name, latitude, longitude);
    }

    @Override
    public void deleteStation(int id) {
        deleteRow(
                SQL_DELETE_STATION,
                stmt -> stmt.setInt(1, id)
        );
    }


    @Override
    public List<Track> getTracks() {
        return getRows(
                SQL_SELECT_TRACKS,
                stmt -> { },
                rs -> {
                    Station station1 = new Station(rs.getInt("s1_id"), rs.getString("s1_name"), rs.getDouble("s1_latitude"), rs.getDouble("s1_longitude"));
                    Station station2 = new Station(rs.getInt("s2_id"), rs.getString("s2_name"), rs.getDouble("s2_latitude"), rs.getDouble("s2_longitude"));
                    return new Track(rs.getInt("id"), station1, station2);
                }
        );
    }

    @Override
    public Track getTrack(int id) {
        return getRow(
                SQL_SELECT_TRACK,
                stmt -> stmt.setInt(1, id),
                rs -> {
                    Station station1 = new Station(rs.getInt("s1_id"), rs.getString("s1_name"), rs.getDouble("s1_latitude"), rs.getDouble("s1_longitude"));
                    Station station2 = new Station(rs.getInt("s2_id"), rs.getString("s2_name"), rs.getDouble("s2_latitude"), rs.getDouble("s2_longitude"));
                    return new Track(rs.getInt("id"), station1, station2);
                }
        );
    }

    @Override
    public Track insertTrack(Station station1, Station station2) {
        int id = insertObservable();
        return insertRow(
                SQL_INSERT_TRACK,
                stmt -> {
                    stmt.setInt(1, id);
                    stmt.setInt(2, station1.getId());
                    stmt.setInt(3, station2.getId());
                },
                rs -> new Track(id, station1, station2)
        );
    }

    @Override
    public Track updateTrack(int id, Station station1, Station station2) {
        updateRow(
                SQL_UPDATE_TRACK,
                stmt -> {
                    stmt.setInt(1, station1.getId());
                    stmt.setInt(2, station2.getId());
                    stmt.setInt(3, id);
                }
        );
        return new Track(id, station1, station2);
    }

    @Override
    public void deleteTrack(int id) {
        deleteRow(
                SQL_DELETE_TRACK,
                stmt -> stmt.setInt(1, id)
        );
    }


    private List<Track> getReservationTracks(int reservationId) {
        return getRows(
                SQL_SELECT_RESERVATION_TRACKS,
                stmt -> stmt.setInt(1, reservationId),
                rs -> getTrack(rs.getInt("track"))
        );
    }
    @Override
    public List<Reservation> getReservations() {
        return getRows(
                SQL_SELECT_RESERVATIONS,
                stmt -> { },
                rs -> {
                    int reservationId = rs.getInt("id");
                    List<Track> route = getReservationTracks(reservationId);
                    return new Reservation(reservationId, rs.getTimestamp("period_start"), rs.getTimestamp("period_stop"), rs.getString("company"), route);
                }
        );
    }

    @Override
    public Reservation getReservation(int id) {
        return getRow(
                SQL_SELECT_RESERVATION,
                stmt -> stmt.setInt(1, id),
                rs -> {
                    int reservationId = rs.getInt("id");
                    List<Track> route = getReservationTracks(reservationId);
                    return new Reservation(reservationId, rs.getTimestamp("period_start"), rs.getTimestamp("period_stop"), rs.getString("company"), route);
                }
        );
    }

    private boolean insertReservationTrack(int reservationId, int trackId) {
        return insertRow(
                SQL_INSERT_RESERVATION_TRACK,
                stmt -> {
                    stmt.setInt(1, reservationId);
                    stmt.setInt(2, trackId);
                },
                rs -> true
        );
    }
    @Override
    public Reservation insertReservation(Timestamp periodStart, Timestamp periodStop, String company, List<Track> route) {
        int id = insertObservable();
        Reservation reservation = insertRow(
                SQL_INSERT_RESERVATION,
                stmt -> {
                    stmt.setInt(1, id);
                    stmt.setTimestamp(2, periodStart);
                    stmt.setTimestamp(3, periodStop);
                    stmt.setString(4, company);
                },
                rs -> new Reservation(id, periodStart, periodStop, company, route)
        );

        for (Track track : route) {
            insertReservationTrack(id, track.getId());
        }

        return reservation;
    }

    private void deleteReservationTracks(int reservationId) {
        deleteRow(
                SQL_DELETE_RESERVATION_TRACKS,
                stmt -> stmt.setInt(1, reservationId)
        );
    }
    @Override
    public void deleteReservation(int id) {
        deleteReservationTracks(id);
        deleteRow(
                SQL_DELETE_RESERVATION,
                stmt -> stmt.setInt(1, id)
        );
    }
}
