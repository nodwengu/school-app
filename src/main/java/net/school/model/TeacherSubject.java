package net.school.model;

public class TeacherSubject {
   private int id;
   private int teacher_id;
   private int subject_id;

   public TeacherSubject(int id, int teacher_id, int subject_id) {
      this.id = id;
      this.teacher_id = teacher_id;
      this.subject_id = subject_id;
   }

   public TeacherSubject(){}

   public void setId(int id) {
      this.id = id;
   }

   public void setTeacher_id(int teacher_id) {
      this.teacher_id = teacher_id;
   }

   public void setSubject_id(int subject_id) {
      this.subject_id = subject_id;
   }

   public int getId() {
      return id;
   }

   public int getTeacher_id() {
      return teacher_id;
   }

   public int getSubject_id() {
      return subject_id;
   }

   @Override
   public String toString() {
      return "TeacherSubject{" +
              "id=" + id +
              ", teacher_id=" + teacher_id +
              ", subject_id=" + subject_id +
              '}';
   }
}
