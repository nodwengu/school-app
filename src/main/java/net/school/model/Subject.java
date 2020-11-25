package net.school.model;

public class Subject {
   private Long id;
   private String subject_name;
  // List<Subject> subjects = new ArrayList<>();

   public Subject(){}

   public Subject(Long id, String subject_name) {
      this.id = id;
      this.subject_name = subject_name;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public void setSubject_name(String subject_name) {
      this.subject_name = subject_name;
   }

   public Long getId() {
      return id;
   }

   public String getSubject_name() {
      return subject_name;
   }

//   public void addSubject(Subject subject) {
//      subjects.add(subject);
//   }

   @Override
   public String toString() {
      return "Subject{" +
              "id=" + id +
              ", subject_name='" + subject_name + '\'' +
              '}';
   }
}
