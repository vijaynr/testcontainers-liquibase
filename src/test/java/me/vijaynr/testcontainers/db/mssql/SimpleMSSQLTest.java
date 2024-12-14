package me.vijaynr.testcontainers.db.mssql;

import me.vijaynr.testcontainers.connections.DBConnectionProvider;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.utility.DockerImageName;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;

public class SimpleMSSQLTest {

    static MSSQLServerContainer<?> mssql;

    static {
        mssql = new MSSQLServerContainer<>(DockerImageName.parse("mcr.microsoft.com/mssql/server:2022-CU14-ubuntu-22.04"))
                .acceptLicense()
                .withPassword("Novell@123")
                .withExposedPorts(1433);
    }

    static DBConnectionProvider connectionProvider;

    @BeforeAll
    static void beforeAll() {
        mssql.start();
        connectionProvider = new DBConnectionProvider(
                mssql.getJdbcUrl(),
                mssql.getUsername(),
                mssql.getPassword()
        );
    }

    @AfterAll
    static void afterAll() {
        mssql.stop();
    }

    @BeforeEach
    void setupSchema() throws SQLException {
        try (Connection connection = connectionProvider.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE test (id INT PRIMARY KEY IDENTITY, name NVARCHAR(255))");
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
    public void testMssqlConnection() throws SQLException {
        try (Connection connection = connectionProvider.getConnection()) {
            assertThat(connection.isValid(2)).isTrue();
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
