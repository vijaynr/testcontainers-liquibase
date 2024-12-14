package me.vijaynr.testcontainers.db.mariadb;

import me.vijaynr.testcontainers.connections.LiquibaseConnectionProvider;
import me.vijaynr.testcontainers.constants.DatabaseContainerImages;
import me.vijaynr.testcontainers.constants.LiquibaseConstants;
import me.vijaynr.testcontainers.utils.LiquibaseUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.utility.DockerImageName;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;

public class LiquibaseMariaDBTest {
    static MariaDBContainer<?> mariadb;

    static {
        mariadb = new MariaDBContainer<>(DockerImageName.parse(DatabaseContainerImages.MARIADB_IMAGE));
    }

    static LiquibaseUtils lb;

    @BeforeAll
    public static void beforeAll() {
        mariadb.start();
        LiquibaseConnectionProvider connectionProvider = new LiquibaseConnectionProvider(
                mariadb.getJdbcUrl(),
                mariadb.getUsername(),
                mariadb.getPassword()
        );
        lb = new LiquibaseUtils(connectionProvider);
    }

    @AfterAll
    public static void afterAll() {
        mariadb.stop();
    }

    @Test
    @DisplayName("Test MariaDB Liquibase Migration for Current Update")
    public void testMariaDBLiquibaseMigrationForCurrentUpdate() throws SQLException {
        lb.setChangelogFile(LiquibaseConstants.MARIADB_CHANGELOG_FILE);
        lb.update();

        try (Connection connection = lb.getConnectionProvider().getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT ID, AUTHOR, FILENAME FROM DATABASECHANGELOG ORDER BY ORDEREXECUTED DESC LIMIT 1")
        ) {
            resultSet.next();
            String lastId = resultSet.getString("ID");
            String lastAuthor = resultSet.getString("AUTHOR");
            String lastFilename = resultSet.getString("FILENAME");

            assertThat(lastId).isNotNull();
            assertThat(lastAuthor).isNotNull();
            assertThat(lastFilename).isEqualTo("db/changelog/mariadb/schema-v2.xml");
        }

        lb.setChangelogFile(LiquibaseConstants.MARIADB_CHANGELOG_UPDATE_FILE);
        lb.update();

        try (Connection connection = lb.getConnectionProvider().getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT ID, AUTHOR, FILENAME FROM DATABASECHANGELOG ORDER BY ORDEREXECUTED DESC LIMIT 1")
        ) {
            resultSet.next();
            String lastId = resultSet.getString("ID");
            String lastAuthor = resultSet.getString("AUTHOR");
            String lastFilename = resultSet.getString("FILENAME");

            assertThat(lastId).isNotNull();
            assertThat(lastAuthor).isNotNull();
            assertThat(lastFilename).isEqualTo("db/changelog/mariadb/schema-v3.xml");
        }
    }
}