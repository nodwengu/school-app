package net.school.dao;

import net.school.model.Lesson;
import net.school.model.Teacher;

import java.util.List;

public interface TeacherDao {
   List<Teacher> getAll();
   boolean addTeacher(Teacher teacher);
   boolean deleteTeacher(Teacher teacher);
   Teacher getById(int id);
   void selectSubject(int teacherId, int subjectId);
   public List<Lesson> getTeacherLessons(int myId);

}
