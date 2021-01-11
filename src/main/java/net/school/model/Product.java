package net.school.model;

import java.util.ArrayList;
import java.util.List;

public class Product {
   private Long id;
   private String productName;
   private int cost;

   private List<Day> days = new ArrayList<>();
   private List<SoldProduct> soldProducts = new ArrayList<>();

   public Product() {}

   public Product(Long id, String productName, int cost) {
      this(productName, cost);
      this.id = id;
   }

   public Product(String productName, int cost) {
      this.productName = productName;
      this.cost = cost;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public void setProductName(String productName) {
      this.productName = productName;
   }

   public void setCost(int cost) {
      this.cost = cost;
   }

   public Long getId() {
      return id;
   }

   public String getProductName() {
      return productName;
   }

   public int getCost() {
      return cost;
   }

   public void addDay(Day day) {
      days.add(day);
   }

   public List<Day> getDays() {
      return days;
   }

   public void addSoldProduct(SoldProduct product) {
      soldProducts.add(product);
   }

   public List<SoldProduct> getSoldProducts() {
      return soldProducts;
   }


   @Override
   public String toString() {
      return "Product{" +
              "id=" + id +
              ", productName='" + productName + '\'' +
              ", cost=" + cost +
              ", days=" + days +
              ", soldProducts=" + soldProducts +
              '}';
   }
}

