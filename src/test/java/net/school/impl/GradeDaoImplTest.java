//package net.school.impl;
//
//import net.school.model.Grade;
//import net.school.model.Lesson;
//import org.jdbi.v3.core.Jdbi;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.sql.SQLException;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class GradeDaoImplTest {
//   private Jdbi jdbi;
//   private GradeDaoImpl gradeDao;
//
//   public Jdbi getJdbiDatabaseConnection(String defaultJdbcUrl) throws URISyntaxException, SQLException {
//      ProcessBuilder processBuilder = new ProcessBuilder();
//      String database_url = processBuilder.environment().get("DATABASE_URL");
//      if (database_url != null) {
//         URI uri = new URI(database_url);
//         String[] hostParts = uri.getUserInfo().split(":");
//         String username = hostParts[0];
//         String password = hostParts[1];
//         String host = uri.getHost();
//         int port = uri.getPort();
//         String path = uri.getPath();
//         String url = String.format("jdbc:postgresql://%s:%s%s", host, port, path);
//         return Jdbi.create(url, username, password);
//      }
//      return Jdbi.create(defaultJdbcUrl);
//   }
//
//   @BeforeEach
//   public void beforeEach() {
//      try {
//         jdbi = getJdbiDatabaseConnection("jdbc:postgresql://localhost/school?user=thando&password=thando123");
//         gradeDao = new GradeDaoImpl(jdbi);
//      } catch (URISyntaxException e) {
//         e.printStackTrace();
//      } catch (SQLException e) {
//         e.printStackTrace();
//      }
//   }
//
//   @Test
//   @DisplayName("Should be able to return all grades")
//   public void getAll() {
//      assertEquals(3, gradeDao.getAll().size());
//   }
//
//   @Test
//   @DisplayName("Should be able to return grade by id")
//   public void getById() {
//      Grade grade = new Grade(1L, "Grade 10");
//      assertEquals("Grade 10", grade.getGradeName());
//   }
//
//   @Test
//   @DisplayName("Should be able to return grade id by name")
//   public void getIdByName() {
//      assertEquals(1, gradeDao.getIdByName("Grade 10"));
//   }
//
//}