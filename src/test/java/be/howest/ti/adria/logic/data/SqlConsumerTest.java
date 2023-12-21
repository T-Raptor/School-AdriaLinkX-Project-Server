package be.howest.ti.adria.logic.data;

import be.howest.ti.adria.logic.exceptions.RepositoryException;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SqlConsumerTest {
    @Test
    void accept() {
        // Arrange
        List<Integer> testBuffer = new ArrayList<>();
        int testNum = 5;
        SqlConsumer<List<Integer>> sqlConsumer = (buffer) -> buffer.add(testNum * 2);

        // Act
        sqlConsumer.accept(testBuffer);

        // Assert
        assertFalse(testBuffer.isEmpty());
        assertEquals(testNum * 2, testBuffer.get(0));
    }

    @Test
    void acceptThrows() {
        // Arrange
        List<Integer> testBuffer = new ArrayList<>();
        SqlConsumer<List<Integer>> sqlConsumer = (buffer) -> {
            throw new SQLException();
        };

        // Act / Assert
        assertThrows(RepositoryException.class, () -> sqlConsumer.accept(testBuffer));
    }
}
