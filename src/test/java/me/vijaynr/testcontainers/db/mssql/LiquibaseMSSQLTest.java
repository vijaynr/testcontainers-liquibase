package me.vijaynr.testcontainers.db.mssql;

import me.vijaynr.testcontainers.connections.LiquibaseConnectionProvider;
import me.vijaynr.testcontainers.constants.DatabaseContainerImages;
import me.vijaynr.testcontainers.constants.LiquibaseConstants;
import me.vijaynr.testcontainers.utils.LiquibaseUtils;
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
        mssql = new MSSQLServerContainer<>(DockerImageName.parse(DatabaseContainerImages.MSSQL_IMAGE));
    }


    static LiquibaseUtils lb;

    @BeforeAll
    public static void beforeAll() {
        mssql.acceptLicense();
        mssql.start();
        LiquibaseConnectionProvider connectionProvider = new LiquibaseConnectionProvider(
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
        lb.setChangelogFile(LiquibaseConstants.MSSQL_CHANGELOG_FILE);
        lb.update();

        try (Connection connection = lb.getConnectionProvider().getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT TOP 1 ID, AUTHOR, FILENAME FROM DATABASECHANGELOG ORDER BY ORDEREXECUTED DESC")
        ) {
            resultSet.next();
            String lastId = resultSet.getString("ID");
            String lastAuthor = resultSet.getString("AUTHOR");
            String lastFilename = resultSet.getString("FILENAME");

            assertThat(lastId).isNotNull();
            assertThat(lastAuthor).isNotNull();
            assertThat(lastFilename).isEqualTo("db/changelog/mssql/schema-v2.xml");
        }

        lb.setChangelogFile(LiquibaseConstants.MSSQL_CHANGELOG_UPDATE_FILE);
        lb.update();

        try (Connection connection = lb.getConnectionProvider().getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT TOP 1 ID, AUTHOR, FILENAME FROM DATABASECHANGELOG ORDER BY ORDEREXECUTED DESC")
        ) {
            resultSet.next();
            String lastId = resultSet.getString("ID");
            String lastAuthor = resultSet.getString("AUTHOR");
            String lastFilename = resultSet.getString("FILENAME");

            assertThat(lastId).isNotNull();
            assertThat(lastAuthor).isNotNull();
            assertThat(lastFilename).isEqualTo("db/changelog/mssql/schema-v3.xml");
        }
    }
}
