package be.howest.ti.adria.logic.data;

import be.howest.ti.adria.logic.exceptions.RepositoryException;

import java.sql.SQLException;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

@FunctionalInterface
public interface SqlConsumer<T> extends Consumer<T> {
    Logger LOGGER = Logger.getLogger(SqlConsumer.class.getName());

    @Override
    default void accept(T t) {
        try {
            acceptThrows(t);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "An error occurred while accepting SQL consumer", e);
            throw new RepositoryException("An error occurred while accepting SQL consumer");
        }
    }

    void acceptThrows(T t) throws SQLException;
}
