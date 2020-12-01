package net.school.dao;

import net.school.model.Learner;
import net.school.model.Lesson;
import net.school.model.Teacher;

import java.util.List;

public interface TeacherDao {
   List<Teacher> getAll();
   boolean addTeacher(Teacher teacher);
   boolean delete(Long teacherId);
   Teacher getById(Long id);
   boolean selectSubject(Long teacherId, Long subjectId);
   public List<Lesson> getTeacherLessons(Long myId);

   List<Teacher> getAllSubjects(Long myId);
   boolean removeTeacherSubject(Long id);
   boolean update(Long id, Teacher teacher);


}
