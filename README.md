Thunderhead
===========

# Running the integration tests
To run the integration tests with maven execute the command

    mvn -U clean verify

There is a template file provided for the properties needed to execute the integration tests.
To run the integration tests please copy this file to src/test/resources/test.properties and and
fill in the appropriate values.

The values to replace are

    thunderhead.query.host=
    thunderhead.update.host=
