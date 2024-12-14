package me.vijaynr.testcontainers.connections;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

public class LiquibaseConnectionProvider  extends DBConnectionProvider {

    private String changelogFile;

    public LiquibaseConnectionProvider(String url, String username, String password, String changelogPath) {
        super(url, username, password);
        this.changelogFile = changelogPath;
    }

    public LiquibaseConnectionProvider(String url, String username, String password) {
        super(url, username, password);
    }

    public void setChangelogFile(String changelogFile) {
        this.changelogFile = changelogFile;
    }

    public String getChangelogFile() {
        return changelogFile;
    }

    public Liquibase getLiquibaseConnection() {
        try {
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(getConnection()));
            return new Liquibase(getChangelogFile(), new ClassLoaderResourceAccessor(), database);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
