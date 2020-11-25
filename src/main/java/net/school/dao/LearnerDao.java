package net.school.dao;

import net.school.model.Grade10Learner;
import net.school.model.Learner;
import net.school.model.LearnerSubject;
import net.school.model.Lesson;

import java.util.List;

public interface LearnerDao {
   boolean registerLearner(Learner learner);
   List<Learner> getAll();
   Learner getById(Long id);
   void selectSubject(Long learnerId, Long subjectId);
   List<LearnerSubject> getLearnerSubjects(Long myId);
   List<Lesson> getAllLessons(Long theId);
   boolean attendLesson(Long learnerId, Long lessonId);


   List<Learner> getAllSubjects(Long myId);
   boolean delete(Learner learner);
   List<Learner> getLearnersFor(Long lessonId);



}
