package net.school.model;

public class Subject {
   private Long id;
   private String subjectName;
  // List<Subject> subjects = new ArrayList<>();

   public Subject(){}

   public Subject(Long id, String subject_name) {
      this.id = id;
      this.subjectName = subject_name;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public void setSubjectName(String subjectName) {
      this.subjectName = subjectName;
   }

   public Long getId() {
      return id;
   }

   public String getSubjectName() {
      return subjectName;
   }

//   public void addSubject(Subject subject) {
//      subjects.add(subject);
//   }

   @Override
   public String toString() {
      return "Subject{" +
              "id=" + id +
              ", subject_name='" + subjectName + '\'' +
              '}';
   }
}
