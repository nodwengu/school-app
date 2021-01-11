package net.school.impl;

import net.school.model.Learner;
import net.school.model.Subject;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LearnerDaoImplTest {
   private Jdbi jdbi;
   private LearnerDaoImpl learnerDao;

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
         learnerDao = new LearnerDaoImpl(jdbi);
      } catch (URISyntaxException e) {
         e.printStackTrace();
      } catch (SQLException e) {
         e.printStackTrace();
      }
   }

//   @Test
//   @Disabled
//   @DisplayName("Should register Grade 10 student")
//   public void registerLearner() {
//      Learner learner = new Learner(1L, "Thanduxolo", "Nodwengu", "thando@gmail.com", 10, 1L);
//      learnerDao.registerLearner(learner);
//      assertEquals(1, learnerDao.getAll());
//   }

   @Test
   @DisplayName("Should be able to return all learners")
   public void getAllLearners() {
      assertEquals(7, learnerDao.getAll().size());
   }

//   @Test
//   @DisplayName("Should Return one learner by id")
//   public void getLearnerById() {
//      Learner learner = learnerDao.getById(4L);
//      assertEquals("Baloyi", learner.getLastName());
//   }

   @Test
   @DisplayName("Should be able to select a subject")
   public void selectSubject() {

     // learnerDao.selectSubject(23L, 4L);
   }

   @Test
   @DisplayName("Should Return lessons for the learner")
   public void shouldReturnLearnerLessons() {
      assertEquals(0, learnerDao.getLessons(3L).size());
   }

   @Test
   @DisplayName("Should be able to delete subject for the learner")
   public void removeSubject() {
      learnerDao.removeLearnerSubject(1L);
   }

   @Test
   @Disabled
   @DisplayName("Should be able to update learner record")
   public void updateLearner() {
      Learner learner1 = learnerDao.getById(22L);
      learner1.setTokens(10);
      learnerDao.update(22L, learner1);
   }

   @Test
   @DisplayName("Should be able to delete subject for the learner")
   public void getLearnersForGrade() {
      assertEquals(6, learnerDao.getLearnersForGrade(1L).size());
   }

   @Test
   @Disabled
   @DisplayName("Should be able to delete one learner from the table")
   public void removeLearner() {
      assertEquals(8, learnerDao.getAll().size());
      learnerDao.delete(25L);
      assertEquals(7, learnerDao.getAll().size());
   }

   @Test
   @DisplayName("Should be able to delete one learner from the table")
   public void getClassMates() {
      assertEquals(3, learnerDao.getClassmates(1L).size());
   }

   @Test
   @DisplayName("Should be able to delete one learner record from the table")
   public void get() {
      List<Subject> subjects = learnerDao.allSubjects(18L);
      System.out.println(subjects.size());
      // assertEquals(3, learnerDao.getClassmates(1L).size());
   }






}