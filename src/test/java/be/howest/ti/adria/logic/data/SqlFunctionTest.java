package be.howest.ti.adria.logic.data;

import be.howest.ti.adria.logic.exceptions.RepositoryException;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class SqlFunctionTest {
    @Test
    void apply() {
        // Arrange
        int testNum = 5;
        SqlFunction<Integer, Integer> sqlFunction = (num) -> num * 2;

        // Act
        int result = sqlFunction.apply(testNum);

        // Assert
        assertEquals(testNum * 2, result);
    }

    @Test
    void applyThrows() {
        // Arrange
        int testNum = 5;
        SqlFunction<Integer, Integer> sqlFunction = (num) -> {
            throw new SQLException();
        };

        // Act / Assert
        assertThrows(RepositoryException.class, () -> sqlFunction.apply(testNum));
    }
}
