package me.vijaynr.testcontainers.utils;

import liquibase.Contexts;
import liquibase.LabelExpression;
import me.vijaynr.testcontainers.connections.LiquibaseConnectionProvider;

public class LiquibaseUtils {

    private final LiquibaseConnectionProvider connectionProvider;

    public LiquibaseUtils(LiquibaseConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    public LiquibaseConnectionProvider getConnectionProvider() {
        return connectionProvider;
    }

    public void update() {
        try {
            this.connectionProvider.getLiquibaseConnection().update(new Contexts(), new LabelExpression());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setChangelogFile(String changelogFile) {
        this.connectionProvider.setChangelogFile(changelogFile);
    }
}
