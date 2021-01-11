package net.school.dao;

import com.google.common.collect.Multimap;
import net.school.model.Lesson;
import net.school.model.Subject;

import java.util.List;

public interface SubjectDao {
   List<Subject> getAll();
   boolean addSubject(Subject subject);
   boolean delete(Long id);
   Subject getById(Long id);
   Long getId(String name);
   //Long getName(Long id);
   boolean update(Subject subject);
   Multimap<Lesson, Subject> getSubjectForLesson(Long id);


//   List<Subject> getAllGrade10();
}
