package net.school.model;

public class Grade10learnerSubject {
   private Long id;
   private Long learner_id;
   private Long subject_id;

   public Grade10learnerSubject(Long id, Long learner_id, Long subject_id) {
      this.id = id;
      this.learner_id = learner_id;
      this.subject_id = subject_id;
   }

   public Grade10learnerSubject(){}

   public void setId(Long id) {
      this.id = id;
   }

   public void setSubject_id(Long subject_id) {
      this.subject_id = subject_id;
   }

   public void setLearner_id(Long learner_id) {
      this.learner_id = learner_id;
   }

   public Long getLearner_id() {
      return learner_id;
   }

   public Long getSubject_id() {
      return subject_id;
   }

   public Long getId() {
      return id;
   }

   @Override
   public String toString() {
      return "Grade10learnerSubject{" +
              "id=" + id +
              ", learner_id=" + learner_id +
              ", subject_id=" + subject_id +
              '}';
   }
}
