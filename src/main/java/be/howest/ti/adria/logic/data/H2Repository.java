package be.howest.ti.adria.logic.data;

import be.howest.ti.adria.logic.data.repositories.*;
import be.howest.ti.adria.logic.domain.*;
import be.howest.ti.adria.logic.domain.observables.*;
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

public class H2Repository implements StationRepository, TrackRepository, ReservationRepository, ShuttleRepository, EventRepository, NotificationRepository {
    private static final Logger LOGGER = Logger.getLogger(H2Repository.class.getName());

    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGITUDE = "longitude";
    private static final String COLUMN_COMPANY = "company";

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

    private static final String SQL_SELECT_SHUTTLES = "select observable_id as id, serial from shuttles;";
    private static final String SQL_SELECT_SHUTTLE = "select observable_id as id, serial from shuttles where observable_id = ?;";
    private static final String SQL_INSERT_SHUTTLE = "insert into shuttles values (?, ?);";

    private static final String SQL_SELECT_EVENTS = "select * from events;";
    private static final String SQL_SELECT_EVENT = "select * from events where id = ?;";
    private static final String SQL_INSERT_EVENT = "insert into events (target, moment, class) values (?, ?, ?);";
    private static final String SQL_INSERT_EVENT_WITH_REASON = "insert into events (target, moment, class, reason) values (?, ?, ?, ?);";
    private static final String SQL_INSERT_LOCAL_EVENT = "insert into events (target, moment, class, local, latitude, longitude) values (?, ?, ?, true, ?, ?);";
    private static final String SQL_INSERT_LOCAL_EVENT_WITH_REASON = "insert into events (target, moment, class, local, latitude, longitude, reason) values (?, ?, ?, true, ?, ?, ?);";

    private static final String SQL_SELECT_NOTIFICATIONS = "select * from notifications;";
    private static final String SQL_INSERT_NOTIFICATION = "insert into notifications (event, company) values (?, ?);";
    private static final String SQL_UPDATE_NOTIFICATION = "update notifications set `read` = ? where event = ? and company = ?;";


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
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (inputStream == null)
                throw new RepositoryException("Could not read file: " + fileName);
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }


    public void cleanUp() {
        if (dbWebConsole.isRunning(false))
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
        generateData(List.of());
    }

    public boolean generateData(List<String> additionalResources) {
        try {
            executeScript("db-create.sql");
            executeScript("db-populate.sql");
            for (String resource : additionalResources) {
                executeScript(resource);
            }
            return true;
        } catch (IOException | SQLException ex) {
            LOGGER.log(Level.SEVERE, "Execution of database scripts failed.", ex);
            return false;
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }


    public <T> T getRow(String statement, SqlConsumer<PreparedStatement> configurator, SqlFunction<ResultSet,T> processor) {
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

    public <T> List<T> getRows(String statement, SqlConsumer<PreparedStatement> configurator, SqlFunction<ResultSet,T> processor) {
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

    public <T> T insertRow(String statement, SqlConsumer<PreparedStatement> configurator, SqlFunction<ResultSet,T> processor) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {

            configurator.accept(stmt);
            stmt.executeUpdate();

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

    public void updateRow(String statement, SqlConsumer<PreparedStatement> configurator) {
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
                rs -> new Station(rs.getInt("id"), rs.getString("name"), rs.getDouble(COLUMN_LATITUDE), rs.getDouble(COLUMN_LONGITUDE))
        );
    }

    @Override
    public Station getStation(int id) {
        return getRow(
                SQL_SELECT_STATION,
                stmt -> stmt.setInt(1, id),
                rs -> new Station(rs.getInt("id"), rs.getString("name"), rs.getDouble(COLUMN_LATITUDE), rs.getDouble(COLUMN_LONGITUDE))
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
                    return new Reservation(reservationId, rs.getTimestamp("period_start"), rs.getTimestamp("period_stop"), rs.getString(COLUMN_COMPANY), route);
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
                    return new Reservation(reservationId, rs.getTimestamp("period_start"), rs.getTimestamp("period_stop"), rs.getString(COLUMN_COMPANY), route);
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


    @Override
    public List<Shuttle> getShuttles() {
        return getRows(
                SQL_SELECT_SHUTTLES,
                stmt -> { },
                rs -> new Shuttle(rs.getInt("id"), rs.getString("serial"))
        );
    }

    @Override
    public Shuttle getShuttle(int id) {
        return getRow(
                SQL_SELECT_SHUTTLE,
                stmt -> stmt.setInt(1, id),
                rs -> new Shuttle(rs.getInt("id"), rs.getString("serial"))
        );
    }

    @Override
    public Shuttle insertShuttle(String serial) {
        int id = insertObservable();
        return insertRow(
                SQL_INSERT_SHUTTLE,
                stmt -> {
                    stmt.setInt(1, id);
                    stmt.setString(2, serial);
                },
                rs -> new Shuttle(id, serial)
        );
    }


    @Override
    public List<Event> getEvents() {
        return getRows(
                SQL_SELECT_EVENTS,
                stmt -> { },
                rs -> {
                    int id = rs.getInt("id");
                    Observable observable = new UnknownObservable(rs.getInt("target"));
                    Timestamp moment = rs.getTimestamp("moment");
                    String subject = rs.getString("class");
                    String reason = rs.getString("reason");
                    if (rs.getBoolean("local")) {
                        double latitude = rs.getDouble(COLUMN_LATITUDE);
                        double longitude = rs.getDouble(COLUMN_LONGITUDE);
                        if (reason == null) {
                            return new LocalEvent(id, observable, moment, subject, latitude, longitude);
                        } else {
                            return new LocalEvent(id, observable, moment, subject, latitude, longitude, reason);
                        }
                    } else {
                        if (reason == null) {
                            return new Event(id, observable, moment, subject);
                        } else {
                            return new Event(id, observable, moment, subject, reason);
                        }
                    }
                });
    }

    @Override
    public Event getEvent(int id) {
        return getRow(
                SQL_SELECT_EVENT,
                stmt -> stmt.setInt(1, id),
                rs -> {
                    int idRemote = rs.getInt("id");
                    Observable observable = new UnknownObservable(rs.getInt("target"));
                    Timestamp moment = rs.getTimestamp("moment");
                    String subject = rs.getString("class");
                    String reason = rs.getString("reason");
                    if (rs.getBoolean("local")) {
                        double latitude = rs.getDouble(COLUMN_LATITUDE);
                        double longitude = rs.getDouble(COLUMN_LONGITUDE);
                        if (reason == null) {
                            return new LocalEvent(idRemote, observable, moment, subject, latitude, longitude);
                        } else {
                            return new LocalEvent(idRemote, observable, moment, subject, latitude, longitude, reason);
                        }
                    } else {
                        if (reason == null) {
                            return new Event(idRemote, observable, moment, subject);
                        } else {
                            return new Event(idRemote, observable, moment, subject, reason);
                        }
                    }
                });
    }

    @Override
    public Event insertEvent(Observable target, Timestamp moment, String what) {
        return insertRow(
                SQL_INSERT_EVENT,
                stmt -> {
                    stmt.setInt(1, target.getId());
                    stmt.setTimestamp(2, moment);
                    stmt.setString(3, what);
                },
                rs -> new Event(rs.getInt("id"), target, moment, what)
        );
    }

    @Override
    public Event insertEvent(Observable target, Timestamp moment, String what, String reason) {
        return insertRow(
                SQL_INSERT_EVENT_WITH_REASON,
                stmt -> {
                    stmt.setInt(1, target.getId());
                    stmt.setTimestamp(2, moment);
                    stmt.setString(3, what);
                    stmt.setString(4, reason);
                },
                rs -> new Event(rs.getInt("id"), target, moment, what, reason)
        );
    }

    @Override
    public LocalEvent insertLocalEvent(Observable target, Timestamp moment, String what, double latitude, double longitude) {
        return insertRow(
                SQL_INSERT_LOCAL_EVENT,
                stmt -> {
                    stmt.setInt(1, target.getId());
                    stmt.setTimestamp(2, moment);
                    stmt.setString(3, what);
                    stmt.setDouble(4, latitude);
                    stmt.setDouble(5, longitude);
                },
                rs -> new LocalEvent(rs.getInt("id"), target, moment, what, latitude, longitude)
        );
    }

    @Override
    public LocalEvent insertLocalEvent(Observable target, Timestamp moment, String what, double latitude, double longitude, String reason) {
        return insertRow(
                SQL_INSERT_LOCAL_EVENT_WITH_REASON,
                stmt -> {
                    stmt.setInt(1, target.getId());
                    stmt.setTimestamp(2, moment);
                    stmt.setString(3, what);
                    stmt.setDouble(4, latitude);
                    stmt.setDouble(5, longitude);
                    stmt.setString(6, reason);
                },
                rs -> new LocalEvent(rs.getInt("id"), target, moment, what, latitude, longitude, reason)
        );
    }


    @Override
    public List<Notification> getNotifications() {
        return getRows(
                SQL_SELECT_NOTIFICATIONS,
                stmt -> { },
                rs -> new Notification(getEvent(rs.getInt("event")), rs.getString(COLUMN_COMPANY), rs.getBoolean("read"))
        );
    }

    @Override
    public Notification insertNotification(int event, String company) {
        return insertRow(
                SQL_INSERT_NOTIFICATION,
                stmt -> {
                    stmt.setInt(1, event);
                    stmt.setString(2, company);
                },
                rs -> new Notification(getEvent(event), company, false)
        );
    }

    @Override
    public Notification updateNotification(int event, String company, boolean read) {
        updateRow(
                SQL_UPDATE_NOTIFICATION,
                stmt -> {
                    stmt.setBoolean(1, read);
                    stmt.setInt(2, event);
                    stmt.setString(3, company);
                }
        );
        return new Notification(getEvent(event), company, read);
    }
}
