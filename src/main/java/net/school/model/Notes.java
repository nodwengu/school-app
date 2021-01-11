package net.school.model;

import java.util.ArrayList;
import java.util.List;

public class Notes {
   private Long id;
   private String title;
   private String source;
   private String notes;
   private Long lessonId;

   private List<Lesson> lessons = new ArrayList<>();
   private  List<Subject> subjects = new ArrayList<>();
   private List<LearnerNotes> learnerNotes = new ArrayList<>();
   private List<Learner> learners = new ArrayList<>();

   public Notes() {}

   public  Notes(Long id,String title, String source, String notes, Long lessonId) {
      this(title, source, notes, lessonId);
      this.id = id;
   }

   public  Notes(String title, String source, String notes, Long lessonId) {
      this.title = title;
      this.source = source;
      this.notes = notes;
      this.lessonId = lessonId;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public void setSource(String source) {
      this.source = source;
   }

   public void setNotes(String notes) {
      this.notes = notes;
   }

   public void setLessonId(Long lessonId) {
      this.lessonId = lessonId;
   }

   public Long getId() {
      return id;
   }

   public String getTitle() {
      return title;
   }

   public String getSource() {
      return source;
   }

   public String getNotes() {
      return notes;
   }

   public Long getLessonId() {
      return lessonId;
   }

   public void addLesson(Lesson lesson) {
      this.lessons.add(lesson);
   }

   public void addSubject(Subject subject) {
      this.subjects.add(subject);
   }

   public void addLearnerNotes(LearnerNotes learnerNote) {
      this.learnerNotes.add(learnerNote);
   }

   public void addLearner(Learner learner) {
      this.learners.add(learner);
   }

   public List<Lesson> getLessons() {
      return lessons;
   }

   public List<Subject> getSubjects() {
      return subjects;
   }

   public List<LearnerNotes> getLearnerNotes() {
      return learnerNotes;
   }

   public List<Learner> getLearners() {
      return learners;
   }

   @Override
   public String toString() {
      return "Notes{" +
              "id=" + id +
              ", title='" + title + '\'' +
              ", source='" + source + '\'' +
              ", notes='" + notes + '\'' +
              ", lessonId=" + lessonId +
              ", lessons=" + lessons +
              ", subjects=" + subjects +
              ", learnerNotes=" + learnerNotes +
              ", learners=" + learners +
              '}';
   }
}
