package net.school.dao;

import net.school.model.Learner;
import net.school.model.Lesson;
import org.jdbi.v3.core.mapper.JoinRow;

import java.util.List;

public interface LessonDao {
   List<Lesson> getAll();
   String addLesson(Lesson lesson);
   boolean delete(Long lessonId);
   Lesson getById(Long id);
   Lesson getByNotesId(Long id);
   List<JoinRow> getLessonForSubject();
   List<Lesson> getTest();

   List<Lesson> getLessonsByGradeAndDay(Long gradeId, Long dayId);
   List<Lesson> getBySubjectAndDay(Long subjectId, Long dayId);

   boolean saveCancelledLesson(Long lessonId);
   List<Lesson> getCancelledLesson();

   List<Lesson> getUserLessonsByGradeAndDay(Long userId, Long gradeId, Long dayId);

}
