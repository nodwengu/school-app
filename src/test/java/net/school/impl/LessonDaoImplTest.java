package net.school.impl;

import net.school.model.Lesson;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class LessonDaoImplTest {
   private Jdbi jdbi;
   private LessonDaoImpl lessonDao;

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
         lessonDao = new LessonDaoImpl(jdbi);
      } catch (URISyntaxException e) {
         e.printStackTrace();
      } catch (SQLException e) {
         e.printStackTrace();
      }
   }

   @Test
   @Disabled
   @DisplayName("Should be able to add new lesson")
   public void addLesson() {
      assertEquals(0, lessonDao.getAll().size());
      Lesson lesson = new Lesson(10L, "testLesson", "10:20", 1L, 1L, 1L);
      lessonDao.addLesson(lesson);
   }

   @Test
   @DisplayName("Should be able to return all lessons")
   public void getLessons() {
      assertEquals(1, lessonDao.getAll().size());
   }

   @Test
   @DisplayName("Should be able to return lessons for grade in specific day")
   public void getLessonsByGradeAndDay() {
      assertEquals(2, lessonDao.getLessonsByGradeAndDay(1L, 1L).size());
   }

   @Test
   @DisplayName("Should be able to delete one lessons lesson record from the table")
   public void removeLesson() {
      assertEquals(26, lessonDao.getAll().size());
      lessonDao.delete(36L);
      assertEquals(26, lessonDao.getAll().size());
   }



}