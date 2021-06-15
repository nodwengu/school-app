package net.school.model;

import java.util.ArrayList;
import java.util.List;

public class Subject {
   private Long id;
   private String subjectName;
  // List<Subject> subjects = new ArrayList<>();
   private String selected;

   public Subject(){}

   public Subject(Long id, String subjectName) {
      this.id = id;
      this.subjectName = subjectName;
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

   public boolean equals(Object o) {
      return  ( (o instanceof Subject) && ((Subject) o).getSubjectName() == subjectName? true : false );
   }

   public void setSelected(String selected) {
      this.selected = selected;
   }

   public String getSelected() {
      return selected;
   }

   @Override
   public String toString() {
      return "Subject{" +
              "id=" + id +
              ", subjectName='" + subjectName + '\'' +
              '}';
   }

   //   @Override
//   public String toString() {
//      return "Subject{" +
//              "id=" + id +
//              ", subject_name='" + subjectName + '\'' +
//              '}';
//   }


//   public static void main(String[] args) {
//      Subject s1 = new Subject(12L, "test");
//      Subject s2 = new Subject(12L, "test");
//      System.out.println(s1.equals(s2));
//      boolean isSubjectEqual = false;
//
//      List<Subject> subjects = new ArrayList<>();
//      subjects.add(s2);
//      subjects.add(new Subject(13L, "tes2"));
//
//      for (Subject s: subjects) {
//         if (s.equals(s1)) {
//            isSubjectEqual = true;
//         }
//      }
//
//      if (isSubjectEqual) {
//         System.out.println("HEAR THEY ARE EQUAL...");
//      }
//   }
}
