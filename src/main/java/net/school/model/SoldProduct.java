package net.school.model;

import java.util.ArrayList;
import java.util.List;

public class SoldProduct {
   private Long id;
   private Long buyerId;
   private String buyerName;
   private String buyerSurname;
   private Long productId;
   private Long dayId;

   private List<Day> days = new ArrayList<>();
   private List<Product> products = new ArrayList<>();


   public SoldProduct() {}

   public void setId(Long id) {
      this.id = id;
   }

   public void setBuyerId(Long buyerId) {
      this.buyerId = buyerId;
   }

   public void setBuyerName(String buyerName) {
      this.buyerName = buyerName;
   }

   public String getBuyerSurname() {
      return buyerSurname;
   }

   public void setProductId(Long productId) {
      this.productId = productId;
   }

   public void setDayId(Long dayId) {
      this.dayId = dayId;
   }

   public Long getId() {
      return id;
   }

   public Long getBuyerId() {
      return buyerId;
   }

   public String getBuyerName() {
      return buyerName;
   }

   public void setBuyerSurname(String buyerSurname) {
      this.buyerSurname = buyerSurname;
   }

   public Long getProductId() {
      return productId;
   }

   public Long getDayId() {
      return dayId;
   }

   public void addDay(Day day) {
      days.add(day);
   }

   public List<Day> getDays() {
      return days;
   }

   public void addProduct(Product product) {
      products.add(product);
   }

   public List<Product> getProducts() {
      return products;
   }

   @Override
   public String toString() {
      return "SoldProduct{" +
              "id=" + id +
              ", buyerId=" + buyerId +
              ", buyerName='" + buyerName + '\'' +
              ", buyerSurname='" + buyerSurname + '\'' +
              ", productId=" + productId +
              ", dayId=" + dayId +
              ", days=" + days +
              ", products=" + products +
              '}';
   }
}
