package net.school.impl;

import net.school.dao.DayDao;
import net.school.dao.LearnerDao;
import net.school.dao.TeacherDao;
import net.school.exceptions.OutOfTokensException;
import net.school.model.*;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductDaoImplTest {
   private Jdbi jdbi;
   private ProductDaoImpl productDao;


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
         productDao = new ProductDaoImpl(jdbi);
      } catch (URISyntaxException e) {
         e.printStackTrace();
      } catch (SQLException e) {
         e.printStackTrace();
      }
   }

   @Test
   @Disabled
   @DisplayName("Should be able to add new product")
   public void addNewProduct() {
      Product product = new Product(10L, "Breakfast", 10);
      productDao.add(product);
      assertEquals(1, productDao.getAll().size());
   }

   @Test
   @DisplayName("Should be able to return all products")
   public void allProducts() {
      assertEquals(1, productDao.getAll().size());
   }

   @Test
   @DisplayName("Should be able to return all products")
   public void OneById() {
      assertEquals("Milk", productDao.getById(5L).getProductName());
   }

   @Test
   @Disabled
   @DisplayName("Should be able to change the value of product")
   public void updateProduct() {
      Product product = productDao.getById(5L);
      product.setProductName("Bar One");
      product.setCost(15);
      productDao.update(5L, product);
      assertEquals("Bar One", productDao.getById(5L).getProductName());
   }

   @Test
   @Disabled
   @DisplayName("Should be able to remove one product")
   public void removeOne() {
      assertEquals(2, productDao.getAll().size());
      productDao.delete(6L);
      assertEquals(1, productDao.getAll().size());

   }


   @Test
   @DisplayName("Should be able to add to sales")
   public void addSales() {
      DayDao dayDao = new DayDaoImpl(jdbi);
      LearnerDao learnerDao = new LearnerDaoImpl(jdbi);
      LocalDate nowDate = LocalDate.now();
      String day = nowDate.getDayOfWeek().toString().toLowerCase();
      day = day.substring(0, 1).toUpperCase() + day.substring(1);
      Long id = dayDao.getIdByName(day);

      Learner learner = learnerDao.getById(18L);

      productDao.addToSales(learner.getId(), learner.getFirstName(), learner.getLastName(), 7L, id);
   }

   @Test
   @DisplayName("Should be able to return daily sales")
   public void getDailySales() {
      System.out.println(productDao.getDailyProductsSold(1L));
   }

   @Test
   @DisplayName("Should be able to return all sales")
   public void getAllSales() {
      System.out.println(productDao.getAllProductsSold());
   }

   @Test
   @DisplayName("Should be able to add to buyer history")
   public void addToBuyerHistory() {
      boolean result = productDao.addToBuyerHistory(30L, 7L, 1L);
      assertEquals(true, result);
   }

   @Test
   @DisplayName("Should be able to add to buyer history")
   public void getBuyerHistory() {
      List<Product> result = productDao.getBuyerHistory(21L);
      System.out.println(result);
      //assertEquals(true, result);
   }

   @Test
   @DisplayName("Should be able to remove buyer_history record history")
   public void deleteFromBuyerHistory() {
      boolean result = productDao.deleteFromBuyerHistory(30L, 7L);
      assertEquals(true, result);
   }



}