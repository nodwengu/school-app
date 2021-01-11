//package net.school.impl;
//
//import net.school.model.Manager;
//import net.school.model.Product;
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
//class ManagerDaoImplTest {
//   private Jdbi jdbi;
//   private ManagerDaoImpl managerDao;
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
//         managerDao = new ManagerDaoImpl(jdbi);
//      } catch (URISyntaxException e) {
//         e.printStackTrace();
//      } catch (SQLException e) {
//         e.printStackTrace();
//      }
//   }
//
//   @Test
//   @Disabled
//   @DisplayName("Should be able to add new manager")
//   public void addNewManager() {
//      Manager manager = new Manager(10L, "Msuthu", "Msuthu", "msuthu@gmail.com");
//      managerDao.add(manager);
//      assertEquals(1, managerDao.getAll().size());
//   }
//
//   @Test
//   @DisplayName("Should be able to return all managers")
//   public void allManagers() {
//      assertEquals(1, managerDao.getAll().size());
//   }
//
//   @Test
//   @DisplayName("Should be able to return one manger")
//   public void OneById() {
//      assertEquals("Msuthu", managerDao.getById(1L).getLastName());
//   }
//
//   @Test
//   @Disabled
//   @DisplayName("Should be able to change the manager record")
//   public void updateManager() {
//      Manager manager = managerDao.getById(1L);
//      manager.setFirstName("New name");
//      manager.setLastName(manager.getLastName());
//      manager.setEmail(manager.getEmail());
//      managerDao.update(1L, manager);
//      assertEquals("New name", managerDao.getById(1L).getFirstName());
//   }
//
//   @Test
//   @Disabled
//   @DisplayName("Should be able to remove manager record")
//   public void removeManager() {
//      assertEquals(2, managerDao.getAll().size());
//      managerDao.delete(1L);
//      assertEquals(1, managerDao.getAll().size());
//   }
//
//   @Test
//   @DisplayName("Should be able to return all tokens for the day")
//   public void returnDailyTokens() {
//      assertEquals(12, managerDao.getDailyTokens(1L));
//   }
//
//
//
//}