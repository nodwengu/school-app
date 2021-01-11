//package net.school.impl;
//
//import net.school.model.Learner;
//import net.school.model.Subject;
//import org.jdbi.v3.core.Jdbi;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.sql.SQLException;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class SubjectDaoImplTest {
//   private Jdbi jdbi;
//   private SubjectDaoImpl subjectDao;
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
//         jdbi = getJdbiDatabaseConnection("jdbc:postgresql://localhost/teachers_learners_lessons_db?user=thando&password=thando123");
//         subjectDao = new SubjectDaoImpl(jdbi);
//      } catch (URISyntaxException e) {
//         e.printStackTrace();
//      } catch (SQLException e) {
//         e.printStackTrace();
//      }
//   }
//
//   @Test
//   @Disabled
//   @DisplayName("Should be able to add a new subject")
//   public void addNewSubject() {
//      assertEquals(10, subjectDao.getAll().size());
//      subjectDao.addSubject(new Subject(100L, "testSubject1"));
//      assertEquals(11, subjectDao.getAll().size());
//   }
//
//   @Test
//   @DisplayName("Should return all subjects")
//   public void getAllSubjects() {
//      assertEquals(10, subjectDao.getAll().size());
//   }
//
//   @Test
//   @DisplayName("Should be able to delete one subject")
//   public void removeSubject() {
//      assertEquals(10, subjectDao.getAll().size());
//      subjectDao.delete(13L);
//      assertEquals(10, subjectDao.getAll().size());
//   }
//
//   @Test
//   @DisplayName("Should be able to return one subjects")
//   public void getSubjectById() {
//      Subject subject = subjectDao.getById(3L);
//      assertEquals("Mathematics", subject.getSubjectName());
//   }
//
//   @Test
//   @DisplayName("Should be able to update subject")
//   public void updateSubject() {
//      Subject subject = subjectDao.getById(12L);
//      subject.setSubjectName("IsiXhosa");
//      subjectDao.update(subject);
//      //assertTrue(subjectDao.update(12L, subject));
//
//      System.out.println(subject);
//   }
//
//
//}