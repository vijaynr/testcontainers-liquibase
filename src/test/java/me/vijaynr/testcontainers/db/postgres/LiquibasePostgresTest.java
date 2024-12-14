package me.vijaynr.testcontainers.db.postgres;

import me.vijaynr.testcontainers.connections.LiquibaseConnectionProvider;
import me.vijaynr.testcontainers.constants.DatabaseContainerImages;
import me.vijaynr.testcontainers.constants.LiquibaseConstants;
import me.vijaynr.testcontainers.utils.LiquibaseUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;

public class LiquibasePostgresTest {

    static PostgreSQLContainer<?> postgres;

    static {
        postgres = new PostgreSQLContainer<>(DatabaseContainerImages.POSTGRES_IMAGE);
    }

    static LiquibaseUtils lb;

    @BeforeAll
    public static void beforeAll() {
        postgres.start();
        LiquibaseConnectionProvider connectionProvider = new LiquibaseConnectionProvider(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword()
        );
        lb = new LiquibaseUtils(connectionProvider);
    }

    @AfterAll
    public static void afterAll() {
        postgres.stop();
    }

    @Test
    @DisplayName("Test Postgres Liquibase Migration for Current Update")
    public void testPostgresLiquibaseMigrationForCurrentUpdate() throws SQLException {
        lb.setChangelogFile(LiquibaseConstants.POSTGRES_CHANGELOG_FILE);
        lb.update();

        try (Connection connection = lb.getConnectionProvider().getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT ID, AUTHOR, FILENAME FROM databasechangelog ORDER BY ORDEREXECUTED DESC LIMIT 1")
        ) {
            resultSet.next();
            String lastId = resultSet.getString("ID");
            String lastAuthor = resultSet.getString("AUTHOR");
            String lastFilename = resultSet.getString("FILENAME");

            assertThat(lastId).isNotNull();
            assertThat(lastAuthor).isNotNull();
            assertThat(lastFilename).isEqualTo("db/changelog/postgres/schema-v2.xml");
        }

        lb.setChangelogFile(LiquibaseConstants.POSTGRES_CHANGELOG_UPDATE_FILE);
        lb.update();

        try (Connection connection = lb.getConnectionProvider().getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT ID, AUTHOR, FILENAME FROM databasechangelog ORDER BY ORDEREXECUTED DESC LIMIT 1")
        ) {
            resultSet.next();
            String lastId = resultSet.getString("ID");
            String lastAuthor = resultSet.getString("AUTHOR");
            String lastFilename = resultSet.getString("FILENAME");

            assertThat(lastId).isNotNull();
            assertThat(lastAuthor).isNotNull();
            assertThat(lastFilename).isEqualTo("db/changelog/postgres/schema-v3.xml");
        }
    }
}
