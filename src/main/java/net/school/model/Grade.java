package net.school.model;

public class Grade {
   private Long id;
   private String gradeName;

   public Grade(){}

   public Grade(Long id, String gradeName) {
      this.id = id;
      this.gradeName = gradeName;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public void setGradeName(String gradeName) {
      this.gradeName = gradeName;
   }

   public Long getId() {
      return id;
   }

   public String getGradeName() {
      return gradeName;
   }

   @Override
   public String toString() {
      return "Grade{" +
              "id=" + id +
              ", gradeName='" + gradeName + '\'' +
              '}';
   }
}
