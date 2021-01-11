package net.school.dao;

import net.school.model.*;

import java.util.List;

public interface LearnerDao {
   boolean registerLearner(Learner learner);
   List<Learner> getAll();
   Learner getById(Long learnerId);
   boolean delete(Long learnerId);
   boolean update(Long learnerId, Learner learner);
   boolean selectSubject(Long learnerId, Long subjectId);
   boolean attendLesson(Long learnerId, Long lessonId);

   boolean removeLearnerSubject(Long subjectId);

   List<Learner> getSubjects(Long learnerId);
   List<Learner> getLearnersAttending(Long lessonId);
   List<Lesson> getLessons(Long learnerId);
   List<Lesson> getLessonsByDay(Long learnerId, Long dayId);
   List<Learner> getLearnersForGrade(Long gradeId);

   List<Learner> getClassmates(Long gradeId);
   List<Lesson> getClassmateLessons(Long learner_id);

   List<Subject> allSubjects(Long learnerId);

}
