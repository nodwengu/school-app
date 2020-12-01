package net.school.dao;

import net.school.model.Lesson;
import org.jdbi.v3.core.mapper.JoinRow;

import java.util.List;

public interface LessonDao {
   List<Lesson> getAll();
   String addLesson(Lesson lesson);
   boolean delete(Long lessonId);
   Lesson getById(Long id);
   List<JoinRow> getLessonForSubject();
   List<Lesson> getTest();


   List<Lesson> getLessonsByGradeAndDay(Long gradeId, Long dayId);
}
