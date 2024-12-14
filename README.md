# Liquibase Migration Test Suite

This project demonstrates how to use Liquibase with Testcontainers to perform database migrations and tests across different database systems. The project is built using Java and Maven.

## Project Structure

- `src/test/resources/db/changelog/mariadb/schema-v3.xml`: Liquibase changelog file for MariaDB.
- `src/test/java/me/vijaynr/testcontainers/db/mariadb/LiquibaseMariaDBTest.java`: Test class for MariaDB Liquibase migrations.
- `src/test/java/me/vijaynr/testcontainers/db/LiquibaseTestSuite.java`: Test suite for running Liquibase migration tests.

## Prerequisites

- Java 11 or higher
- Maven 3.6.0 or higher
- Docker

## Running the Tests

1. Clone the repository:
    ```sh
    git clone <repository-url>
    cd <repository-directory>
    ```

2. Build the project:
    ```sh
    mvn clean install
    ```

3. Run the tests:
    ```sh
    mvn test
    ```

## Project Dependencies

- `org.testcontainers:testcontainers`: Testcontainers library for managing Docker containers in tests.
- `org.liquibase:liquibase-core`: Liquibase library for database migrations.
- `org.junit.jupiter:junit-jupiter-api`: JUnit 5 API for writing tests.
- `org.assertj:assertj-core`: AssertJ library for fluent assertions.

## License

This project is licensed under the MIT License. See the `LICENSE` file for details.