package com.wizeline;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MethodsTest {
    private static final String EXPECTED_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiYWRtaW4ifQ.StuYX978pQGnCeeaj2E1yBYwQvZIodyDTCJWXdsxBGI";
    private static final String TEST_SECRET = "my2w7wjd7yXF64FIADfJxNs1oupTGAuW";
    private static final DBConnection dbConnection = new TestSQLConnection();

    @Test
    void generateToken() {
        Assertions.assertEquals(
                EXPECTED_TOKEN,
                Methods.generateToken(dbConnection.getConnection(), "admin", "secret", TEST_SECRET));
    }

    @Test
    void accessData() {
        Assertions.assertEquals(
                "You are under protected data",
                Methods.accessData(dbConnection.getConnection(), EXPECTED_TOKEN, TEST_SECRET));
    }
}