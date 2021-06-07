package com.lyferise.cube.node;

import org.junit.jupiter.api.Test;

import java.sql.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class DatabaseIntegrationTest {
    private final String dbUsername = "test";
    private final String dbPassword = "test";
    private final String dbName = "test";

    @Test
    public void shouldInsertThenSelectRow() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + dbName + "?allowPublicKeyRetrieval=true",
                    dbUsername, dbPassword);
            Statement stmt = con.createStatement();

            // insert
            int insertInt = 666;
            int insert = stmt.executeUpdate("insert into integration (id, value) values (1, " + insertInt + ");");
            assertThat(1, is(insert));

            // read back
            ResultSet select = stmt.executeQuery("select value from integration;");
            //TODO: check if select succeeded
            select.next();
            int selectInt = select.getInt("value");

            //cleanup
            stmt.executeUpdate("delete from integration where value=666;");
            con.close();

            // verify
            assertThat(selectInt, is(insertInt));
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
