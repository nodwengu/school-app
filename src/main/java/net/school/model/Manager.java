package net.school.model;

import java.time.LocalDate;

public class Manager extends Person{
   public Manager() {}

   public Manager(Long id, String first_name, String last_name, String email) {
      super(id, first_name, last_name,email);
   }

   @Override
   public String toString() {
      return "Manager{" +
              "id=" + getId() +
              ", firstName='" + getFirstName() + '\'' +
              ", lastName='" + getLastName() + '\'' +
              ", email='" + getEmail() + '\'' +
              '}';
   }

   public static void main(String[] args) {
      Manager manager = new Manager();
      System.out.println(manager);

      LocalDate nowDate = LocalDate.now();
      System.out.println(nowDate.getDayOfWeek());
   }
}
