package net.school.model;

import java.util.ArrayList;
import java.util.List;

public class Teacher extends Person {
   private int tokens;
   private List<TeacherSubject> teacherSubjects = new ArrayList<>();
   private List<Subject> subjects = new ArrayList<>();
//   private List<Lesson> lessons = new ArrayList<>();

   public Teacher() {}

   public Teacher(Long id, String first_name, String last_name, String email, int tokens) {
      super(id, first_name, last_name,email);
      this.tokens = tokens;
   }

   public void setTokens(int tokens) {
      this.tokens = tokens;
   }

   public int getTokens() {
      return tokens;
   }

   public void addTeacherSubject(TeacherSubject ts) {
      teacherSubjects.add(ts);
   }

   public void addSubject(Subject subject) {
      subjects.add(subject);
   }

   public List<TeacherSubject> getTeacherSubjects() {
      return teacherSubjects;
   }

   public List<Subject> getSubjects() {
      return subjects;
   }

   public void increaseTokens() {
      this.tokens += 5;
   }

   public void decreaseTokens() {
      this.tokens -= 5;
   }

   //   @Override
//   public String toString() {
//      return "Teacher{" +
//              "tokens=" + tokens +
//              ", teacherSubjects=" + teacherSubjects +
//              ", subjects=" + subjects +
//              "} " + super.toString();
//   }

   @Override
   public String toString() {
      return "Teacher{" +
              "id=" + getId() +
              ", firstName='" + getFirstName() + '\'' +
              ", lastName='" + getLastName() + '\'' +
              ", email='" + getEmail() + '\'' +
              ", tokens=" + getTokens() + '\'' +
              ", teacherSubjects=" + teacherSubjects +
              ", subjects=" + subjects +
              '}';
   }
//
//   public static void main(String[] args) {
//      System.out.println(new Teacher(1L, "Thanduxolo", "Nodwengu", "thando@gmail.com", 10));
//   }


//   public void setTeacherSubjects(List<TeacherSubject> teacherSubjects) {
//      this.teacherSubjects = teacherSubjects;
//   }
//
//   public List<TeacherSubject> getTeacherSubjects() {
//      return teacherSubjects;
//   }
//

//   public List<Subject> getSubjects() {
//      return subjects;
//   }
//
//   public void addLesson(Lesson lesson) {
//      lessons.add(lesson);
//   }
//
//   public List<Lesson> getLessons() {
//      return lessons;
//   }

//   @Override
//   public String toString() {
//      return "Teacher{" + "id='" + getId() + '\'' +
//              ", firstName='" + getFirstName() + '\'' +
//              ", lastName='" + getLastName() + '\'' +
//              ", Tokens='" + getTokens() + '\'' +
//              '}';
//   }

//   public static void main(String[] args) {
//      System.out.println(new Teacher(1, "Thomas", "Khumalo", "thomas@gmail.com", 20));
//   }
}
