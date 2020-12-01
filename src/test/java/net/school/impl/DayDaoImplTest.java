package net.school.impl;

import net.school.model.Day;
import net.school.model.Grade;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DayDaoImplTest {
   private Jdbi jdbi;
   private DayDaoImpl dayDao;

   public Jdbi getJdbiDatabaseConnection(String defaultJdbcUrl) throws URISyntaxException, SQLException {
      ProcessBuilder processBuilder = new ProcessBuilder();
      String database_url = processBuilder.environment().get("DATABASE_URL");
      if (database_url != null) {
         URI uri = new URI(database_url);
         String[] hostParts = uri.getUserInfo().split(":");
         String username = hostParts[0];
         String password = hostParts[1];
         String host = uri.getHost();
         int port = uri.getPort();
         String path = uri.getPath();
         String url = String.format("jdbc:postgresql://%s:%s%s", host, port, path);
         return Jdbi.create(url, username, password);
      }
      return Jdbi.create(defaultJdbcUrl);
   }

   @BeforeEach
   public void beforeEach() {
      try {
         jdbi = getJdbiDatabaseConnection("jdbc:postgresql://localhost/school?user=thando&password=thando123");
         dayDao = new DayDaoImpl(jdbi);
      } catch (URISyntaxException e) {
         e.printStackTrace();
      } catch (SQLException e) {
         e.printStackTrace();
      }
   }

   @Test
   @DisplayName("Should be able to return all grades")
   public void getAll() {
      assertEquals(5, dayDao.getAll().size());
   }

   @Test
   @DisplayName("Should be able to return grade by id")
   public void getById() {
      Day day = dayDao.getById(1L);
      assertEquals("Monday", day.getDayName());
   }

   @Test
   @DisplayName("Should be able to return grade id by name")
   public void getIdByName() {
      assertEquals(3, dayDao.getIdByName("Wednesday"));
   }

}