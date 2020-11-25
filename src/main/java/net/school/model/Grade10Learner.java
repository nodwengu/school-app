package net.school.model;

import java.util.ArrayList;
import java.util.List;

public class Grade10Learner extends Person {
   private List<Grade10learnerSubject> learnerSubjects = new ArrayList<>();
   private List<Subject> subjects = new ArrayList<>();
   private List<LearnerLessonAttendant> learnersFor = new ArrayList<>();

   public Grade10Learner(){}

   public Grade10Learner(Long id, String firstName, String lastName, String email, int tokens) {
      super(id, firstName, lastName, email, tokens);
   }

   public void addSubject(Subject subject) {
      subjects.add(subject);
   }
   public void addLearnerSubject(Grade10learnerSubject subject) {
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

   @Override
   public String toString() {
      return "Grade10Learner{" +
              "id=" + this.getId() +
              ", firstName='" + getFirstName() + '\'' +
              ", lastName='" + getLastName() + '\'' +
              ", email='" + getEmail() + '\'' +
              ", tokens=" + getTokens() + '\'' +
              ", subjects=" + subjects +
              '}';
   }
}
