package me.testcontainers.db.mssql;

import me.testcontainers.connections.LiquibaseConnectionProvider;
import me.testcontainers.utils.LiquibaseUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.utility.DockerImageName;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;

public class LiquibaseMSSQLTest {
    static MSSQLServerContainer<?> mssql;

    static {
        mssql = new MSSQLServerContainer<>(DockerImageName.parse("mcr.microsoft.com/mssql/server:2022-CU14-ubuntu-22.04"))
                .acceptLicense()
                .withPassword("Novell@123")
                .withExposedPorts(1433);
    }


    static LiquibaseConnectionProvider connectionProvider;
    static LiquibaseUtils lb;

    @BeforeAll
    public static void beforeAll() {
        mssql.start();
        connectionProvider = new LiquibaseConnectionProvider(
                mssql.getJdbcUrl(),
                mssql.getUsername(),
                mssql.getPassword()
        );
        lb = new LiquibaseUtils(connectionProvider);
    }

    @AfterAll
    public static void afterAll() {
        mssql.stop();
    }

    @Test
    @DisplayName("Test SQL Server Liquibase Migration for Current Update")
    public void testSQLServerLiquibaseMigrationForCurrentUpdate() throws SQLException {
        lb.setChangelogFile("liquibase/mssql/schema-master.xml");
        lb.update();

        try (Connection connection = connectionProvider.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT TOP 1 ID, AUTHOR, FILENAME FROM DATABASECHANGELOG ORDER BY ORDEREXECUTED DESC")
        ) {
            resultSet.next();
            String lastId = resultSet.getString("ID");
            String lastAuthor = resultSet.getString("AUTHOR");
            String lastFilename = resultSet.getString("FILENAME");

            assertThat(lastId).isNotNull();
            assertThat(lastAuthor).isNotNull();
            assertThat(lastFilename).isEqualTo("liquibase/mssql/schema-v2.xml");
        }

        lb.setChangelogFile("liquibase/mssql/schema-master-update.xml");
        lb.update();

        try (Connection connection = connectionProvider.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT TOP 1 ID, AUTHOR, FILENAME FROM DATABASECHANGELOG ORDER BY ORDEREXECUTED DESC")
        ) {
            resultSet.next();
            String lastId = resultSet.getString("ID");
            String lastAuthor = resultSet.getString("AUTHOR");
            String lastFilename = resultSet.getString("FILENAME");

            assertThat(lastId).isNotNull();
            assertThat(lastAuthor).isNotNull();
            assertThat(lastFilename).isEqualTo("liquibase/mssql/schema-v3.xml");
        }
    }
}
