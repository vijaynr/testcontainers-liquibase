package me.testcontainers.db.postgres;

import me.testcontainers.connections.DBConnectionProvider;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;


public class SimplePostgresTest {

    static PostgreSQLContainer<?> postgres;

    static {
        postgres = new PostgreSQLContainer<>("postgres:13-alpine")
                .withDatabaseName("filr")
                .withUsername("filr")
                .withPassword("novell")
                .withExposedPorts(5432);
    }

    static DBConnectionProvider connectionProvider;

    @BeforeAll
    static void beforeAll() {
        postgres.start();
        connectionProvider = new DBConnectionProvider(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword()
        );
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }


    @BeforeEach
    void setupSchema() throws SQLException {
        try (Connection connection = connectionProvider.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE test (id SERIAL PRIMARY KEY, name VARCHAR(255))");
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        try (Connection connection = connectionProvider.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("DROP TABLE IF EXISTS test");
        }
    }

    @Test
    public void testSimpleQuery() throws SQLException {
        try (ResultSet resultSet = connectionProvider.getConnection().createStatement().executeQuery("SELECT 1")) {
            resultSet.next();
            assertThat(resultSet.getInt(1)).isEqualTo(1);
        }
    }

    @Test
    public void testPostgresqlConnection() throws SQLException {
        try (Connection connection = connectionProvider.getConnection();
             Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery("SELECT 1")) {
                resultSet.next();
                assertThat(resultSet.getInt(1)).isEqualTo(1);
            }
        }
    }

    @Test
    public void testInsertOperation() throws SQLException {
        try (Connection connection = connectionProvider.getConnection();
             Statement statement = connection.createStatement()) {
            int rowsInserted = statement.executeUpdate("INSERT INTO test (name) VALUES ('testName')");
            assertThat(rowsInserted).isEqualTo(1);
        }
    }

    @Test
    public void testQueryOperation() throws SQLException {
        try (Connection connection = connectionProvider.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("INSERT INTO test (name) VALUES ('testName')");
            try (ResultSet resultSet = statement.executeQuery("SELECT name FROM test WHERE name = 'testName'")) {
                assertThat(resultSet.next()).isTrue();
                assertThat(resultSet.getString("name")).isEqualTo("testName");
            }
        }
    }

    @Test
    public void testUpdateOperation() throws SQLException {
        try (Connection connection = connectionProvider.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("INSERT INTO test (name) VALUES ('testName')");
            int rowsUpdated = statement.executeUpdate("UPDATE test SET name = 'updatedName' WHERE name = 'testName'");
            assertThat(rowsUpdated).isEqualTo(1);
            try (ResultSet resultSet = statement.executeQuery("SELECT name FROM test WHERE name = 'updatedName'")) {
                assertThat(resultSet.next()).isTrue();
                assertThat(resultSet.getString("name")).isEqualTo("updatedName");
            }
        }
    }

    @Test
    public void testDeleteOperation() throws SQLException {
        try (Connection connection = connectionProvider.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("INSERT INTO test (name) VALUES ('testName')");
            int rowsDeleted = statement.executeUpdate("DELETE FROM test WHERE name = 'testName'");
            assertThat(rowsDeleted).isEqualTo(1);
            try (ResultSet resultSet = statement.executeQuery("SELECT name FROM test WHERE name = 'testName'")) {
                assertThat(resultSet.next()).isFalse();
            }
        }
    }
}