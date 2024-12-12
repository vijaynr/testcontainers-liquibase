package me.testcontainers.db;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import me.testcontainers.db.mssql.LiquibaseMSSQLTest;
import me.testcontainers.db.postgres.LiquibasePostgresTest;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Liquibase Migration Test Suite")
@SelectClasses({LiquibaseMSSQLTest.class, LiquibasePostgresTest.class})
public class LiquibaseTestSuite {
}
