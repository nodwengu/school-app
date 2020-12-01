package net.school.dao;

import net.school.model.*;

import java.util.List;

public interface LearnerDao {
   boolean registerLearner(Learner learner);
   List<Learner> getAll();
   Learner getById(Long learnerId);
   boolean delete(Long learnerId);
   public boolean update(Long learnerId, Learner learner);
   void selectSubject(Long learnerId, Long subjectId);
   List<Lesson> getLessons(Long learnerId);
   boolean attendLesson(Long learnerId, Long lessonId);
   boolean removeLearnerSubject(Long subjectId);

   List<Learner> getSubjects(Long learnerId);
   List<Learner> getLearnersAttending(Long lessonId);
   List<Lesson> getLessonsForDay(Long learnerId, Long dayId);
   List<Learner> getLearnersForGrade(Long gradeId);





}
