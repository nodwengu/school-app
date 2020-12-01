package net.school.impl;

import net.school.model.Learner;
import net.school.model.Teacher;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class TeacherDaoImplTest {
   private Jdbi jdbi;
   private TeacherDaoImpl teacherDao;

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
         teacherDao = new TeacherDaoImpl(jdbi);
      } catch (URISyntaxException e) {
         e.printStackTrace();
      } catch (SQLException e) {
         e.printStackTrace();
      }
   }

   @Test
   @Disabled
   @DisplayName("Should be able to add new teacher")
   public void addTeacher() {
      assertEquals(0, teacherDao.getAll().size());
      Teacher teacher = new Teacher(1L, "Sibusiso", "Mazibuko", "sibu@gmail.com", 10);
      teacherDao.addTeacher(teacher);
   }

   @Test
   @DisplayName("Should be return all teachers")
   public void getAll() {
      assertEquals(1, teacherDao.getAll().size());
   }

   @Test
   @DisplayName("Should be return teacher by id")
   public void getById() {
      Teacher teacher = teacherDao.getById(1L);
      assertEquals("Mazibuko", teacher.getLastName());
   }

   @Test
   @Disabled
   @DisplayName("Should be delete one teacher record from the table")
   public void removeOne() {
      assertEquals(6, teacherDao.getAll().size());
      teacherDao.delete(7L);
      assertEquals(5, teacherDao.getAll().size());
   }

//   @Test
//   @DisplayName("Should be able to select a subject")
//   public void selectSubject() {
//      teacherDao.selectSubject(1L, 1L);
//     // assertEquals("Mazibuko", );
//   }

}