package net.school.model;

public class LearnerLessonAttendant {
   private int id;
   private int learner_id;
   private int lesson_id;

   public LearnerLessonAttendant(int id, int learner_id, int lesson_id) {
      this.id = id;
      this.learner_id = learner_id;
      this.lesson_id = lesson_id;
   }

   public LearnerLessonAttendant() { }

   public void setId(int id) {
      this.id = id;
   }

   public void setLearner_id(int learner_id) {
      this.learner_id = learner_id;
   }

   public void setLesson_id(int lesson_id) {
      this.lesson_id = lesson_id;
   }

   public int getId() {
      return id;
   }

   public int getLearner_id() {
      return learner_id;
   }

   public int getLesson_id() {
      return lesson_id;
   }

   @Override
   public String toString() {
      return "LearnerLessonAttendant{" +
              "id=" + id +
              ", learner_id=" + learner_id +
              ", lesson_id=" + lesson_id +
              '}';
   }
}
