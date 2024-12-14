package me.vijaynr.testcontainers;

import me.vijaynr.testcontainers.db.mariadb.LiquibaseMariaDBTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import me.vijaynr.testcontainers.db.mssql.LiquibaseMSSQLTest;
import me.vijaynr.testcontainers.db.postgres.LiquibasePostgresTest;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Liquibase Migration Test Suite")
@SelectClasses({LiquibaseMSSQLTest.class, LiquibasePostgresTest.class, LiquibaseMariaDBTest.class})
public class LiquibaseTestSuite {
    // This class will not have any test methods
}
