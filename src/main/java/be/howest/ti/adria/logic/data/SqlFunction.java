package be.howest.ti.adria.logic.data;

import be.howest.ti.adria.logic.exceptions.RepositoryException;

import java.sql.SQLException;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

@FunctionalInterface
public interface SqlFunction<T, R> extends Function<T, R> {
    Logger LOGGER = Logger.getLogger(SqlFunction.class.getName());


    @Override
    default R apply(T t) {
        try {
            return applyThrows(t);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "An error occurred while applying SQL function", e);
            throw new RepositoryException("An error occurred while applying SQL function");
        }
    }

    R applyThrows(T t) throws SQLException;
}
