package net.school.model;

public class LearnerSubject {
   private Long id;
   private Long learnerId;
   private Long subjectId;

   public LearnerSubject(Long id, Long learner_id, Long subject_id) {
      this.id = id;
      this.learnerId = learner_id;
      this.subjectId = subject_id;
   }

   public LearnerSubject(){}

   public void setId(Long id) {
      this.id = id;
   }

   public void setSubjectId(Long subjectId) {
      this.subjectId = subjectId;
   }

   public void setLearnerId(Long learnerId) {
      this.learnerId = learnerId;
   }

   public Long getLearnerId() {
      return learnerId;
   }

   public Long getSubjectId() {
      return subjectId;
   }

   public Long getId() {
      return id;
   }

   @Override
   public String toString() {
      return "LearnerSubject{" +
              "id=" + id +
              ", learner_id=" + learnerId +
              ", subject_id=" + subjectId +
              '}';
   }
}
