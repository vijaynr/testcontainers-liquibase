package me.vijaynr.testcontainers;

import me.vijaynr.testcontainers.db.mariadb.SimpleMariaDBTest;
import me.vijaynr.testcontainers.db.mssql.SimpleMSSQLTest;
import me.vijaynr.testcontainers.db.postgres.SimplePostgresTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Simple Database Test Suite")
@SelectClasses({SimplePostgresTest.class, SimpleMariaDBTest.class, SimpleMSSQLTest.class})
public class SimpleDatabaseTestSuite {
    // This class will not have any test methods
}
