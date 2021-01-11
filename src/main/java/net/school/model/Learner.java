package net.school.model;

import java.util.ArrayList;
import java.util.List;

public class Learner extends Person {
   private Long gradeId;
   private int tokens;
   private List<LearnerSubject> learnerSubjects = new ArrayList<>();
   private List<Subject> subjects = new ArrayList<>();
   private List<LearnerLessonAttendant> learnersFor = new ArrayList<>();
   private List<Grade> grades = new ArrayList<>();

   public Learner(){}

   public Learner(Long id, String firstName, String lastName, String email, int tokens, Long gradeId) {
      super(id, firstName, lastName, email);
      this.gradeId = gradeId;
      this.tokens = tokens;
   }

   public void increaseTokens(int value) {
      this.tokens += value;
   }

   public void decreaseTokens(int value) {
      this.tokens -= value;
   }

   public void setGradeId(Long gradeId) {
      this.gradeId = gradeId;
   }

   public Long getGradeId() {
      return gradeId;
   }

   public void setTokens(int tokens) {
      this.tokens = tokens;
   }

   public int getTokens() {
      return tokens;
   }

   public void addSubject(Subject subject) {
      subjects.add(subject);
   }
   public void addLearnerSubject(LearnerSubject subject) {
      learnerSubjects.add(subject);
   }
   public List<Subject> getSubjects() {
      return subjects;
   }

   public void addLearnerFor(LearnerLessonAttendant learner) {
      learnersFor.add(learner);
   }
   public List<LearnerLessonAttendant> getLearnerFor() {
      return learnersFor;
   }

   public void addGrade(Grade grade) {
      grades.add(grade);
   }

   public List<Grade> getGrades() {
      return grades;
   }

   @Override
   public String toString() {
      return "Learner{" +
              "id=" + this.getId() +
              ", firstName='" + getFirstName() + '\'' +
              ", lastName='" + getLastName() + '\'' +
              ", email='" + getEmail() + '\'' +
              ", subjects='" + subjects +
              ", grade='" + gradeId + '\'' +
              ", tokens='" + tokens + '\'' +
              ", grades='" + grades +
              '}';
   }

}