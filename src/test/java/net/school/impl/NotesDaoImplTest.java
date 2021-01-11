//package net.school.impl;
//
//import net.school.model.LearnerNotes;
//import net.school.model.Notes;
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
//class NotesDaoImplTest {
//   private Jdbi jdbi;
//   private NotesDaoImpl notesDao;
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
//         notesDao = new NotesDaoImpl(jdbi);
//      } catch (URISyntaxException e) {
//         e.printStackTrace();
//      } catch (SQLException e) {
//         e.printStackTrace();
//      }
//   }
//
//   @Test
//   @Disabled
//   @DisplayName("Should be able to add new Notes")
//   public void add() {
//      Notes notes = new Notes(10L, "The concepts of algebraic expressions", "attended", "algebra notes", 9L);
//      notesDao.add(notes);
//      assertEquals(1, notesDao.getAll().size());
//   }
//
//   @Test
//   @DisplayName("Should be able to return all notes")
//   public void getAll() {
//      assertEquals(1, notesDao.getAll().size());
//   }
//
//   @Test
//   @Disabled
//   @DisplayName("Should be able to add learner notes")
//   public void addLearnerNotes() {
//      assertEquals(true, notesDao.addLearnerNotes(18L, 6L));
//   }
//
//   @Test
//   @DisplayName("Should be able get notes id by lesson")
//   public void getNotesId() {
//      assertEquals(2, notesDao.getIdByLessonId(41L));
//   }
//
//   @Test
//   @DisplayName("Should be able get notes for a specific learner")
//   public void getLearnerNotes() {
//      assertEquals(1, notesDao.getLearnerNotes(18L).size());
//   }
//
//   @Test
//   @DisplayName("Should be able to return notes by id")
//   public void getById() {
//      assertEquals("English notes title", notesDao.getById(10L).getTitle());
//   }
//
//
//   @Test
//   @DisplayName("Should be able to change source")
//   public void changeSource() {
//      assertEquals(true, notesDao.updateSource(18L, 1L, "attended"));
//   }
//
//
//
//
//}