package me.vijaynr.testcontainers.db;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import me.vijaynr.testcontainers.db.mssql.LiquibaseMSSQLTest;
import me.vijaynr.testcontainers.db.postgres.LiquibasePostgresTest;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Liquibase Migration Test Suite")
@SelectClasses({LiquibaseMSSQLTest.class, LiquibasePostgresTest.class})
public class LiquibaseTestSuite {
}
