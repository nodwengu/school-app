package net.school.model;

public class TeacherSubject {
   private Long id;
   private Long teacherId;
   private Long subjectId;

   public TeacherSubject(Long id, Long teacherId, Long subjectId) {
      this.id = id;
      this.teacherId = teacherId;
      this.subjectId = subjectId;
   }

   public TeacherSubject(){}

   public void setId(Long id) {
      this.id = id;
   }

   public void setTeacherId(Long teacherId) {
      this.teacherId = teacherId;
   }

   public void setSubjectId(Long subjectId) {
      this.subjectId = subjectId;
   }

   public Long getId() {
      return id;
   }

   public Long getTeacherId() {
      return teacherId;
   }

   public Long getSubjectId() {
      return subjectId;
   }

   @Override
   public String toString() {
      return "TeacherSubject{" +
              "id=" + id +
              ", teacherId=" + teacherId +
              ", subjectId=" + subjectId +
              '}';
   }

}
