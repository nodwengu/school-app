package net.school.model;

public class LearnerSubject {
   private int id;
   private int learner_id;
   private int subject_id;

   public LearnerSubject(int id, int learner_id, int subject_id) {
      this.id = id;
      this.learner_id = learner_id;
      this.subject_id = subject_id;
   }

   public LearnerSubject(){}

   public void setId(int id) {
      this.id = id;
   }

   public void setSubject_id(int subject_id) {
      this.subject_id = subject_id;
   }

   public void setLearner_id(int learner_id) {
      this.learner_id = learner_id;
   }

   public int getLearner_id() {
      return learner_id;
   }

   public int getSubject_id() {
      return subject_id;
   }

   public int getId() {
      return id;
   }

   @Override
   public String toString() {
      return "LearnerSubject{" +
              "id=" + id +
              ", learner_id=" + learner_id +
              ", subject_id=" + subject_id +
              '}';
   }
}
