package net.school.dao;

import net.school.model.Learner;
import net.school.model.Lesson;

import java.util.List;

public interface LearnerDaoCopy {
   List<Learner> getAll();
   boolean add(Learner learner);
   boolean delete(Learner learner);
   Learner getById(Long id);
   void selectSubject(Long learnerId, Long subjectId);
   List<Lesson> getAllLessons(Long myId);
   List<Learner> getAllSubjects(Long myId);
   boolean attendLesson(Long learnerId, Long lessonId);
   List<Learner> getLearnersFor(Long lessonId);
}
