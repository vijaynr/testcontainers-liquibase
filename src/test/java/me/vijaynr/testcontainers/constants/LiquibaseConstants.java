package me.vijaynr.testcontainers.constants;

public class LiquibaseConstants {
    // changelog file paths
    public static final String POSTGRES_CHANGELOG_FILE = "db/changelog/postgres/db.changelog-master.xml";
    public static final String MSSQL_CHANGELOG_FILE = "db/changelog/mssql/db.changelog-master.xml";
    public static final String MARIADB_CHANGELOG_FILE = "db/changelog/mariadb/db.changelog-master.xml";

    // update changelog file paths
    public static final String POSTGRES_CHANGELOG_UPDATE_FILE = "db/changelog/postgres/db.changelog-master-update.xml";
    public static final String MSSQL_CHANGELOG_UPDATE_FILE = "db/changelog/mssql/db.changelog-master-update.xml";
    public static final String MARIADB_CHANGELOG_UPDATE_FILE = "db/changelog/mariadb/db.changelog-master-update.xml";
}
