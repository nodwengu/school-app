package net.school.impl;

import net.school.dao.TeacherDao;
import net.school.model.Lesson;
import net.school.model.Subject;
import net.school.model.Teacher;
import net.school.model.TeacherSubject;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.BeanMapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class TeacherDaoImpl implements TeacherDao {
   private Jdbi jdbi;
   private List<Teacher> teachers;
   private List<Lesson> teacherLessons = new ArrayList<>();

   public TeacherDaoImpl(Jdbi jdbi) {
      this.jdbi = jdbi;
   }

   public TeacherDaoImpl() {}

   @Override
   public List<Teacher> getAll() {
      return jdbi.withHandle( (handle) -> handle.createQuery("SELECT id, first_name, last_name, email, tokens FROM teacher")
           .mapToBean(Teacher.class)
           .list() );
   }

   @Override
   public boolean addTeacher(Teacher teacher) {
      jdbi.useHandle( (handle) -> handle.execute("INSERT INTO teacher (first_name, last_name, email, tokens) VALUES(?, ?, ?, ?)",
              teacher.getFirstName(),
              teacher.getLastName(),
              teacher.getEmail(),
              teacher.getTokens() )
      );
      return true;
   }

   public int testDivide(int x, int y) {
      return x / y;
   }

   @Override
   public Teacher getById(int id) {
        return jdbi.withHandle( (handle) -> handle.createQuery("SELECT id, first_name, last_name, email, tokens FROM teacher WHERE id = :id")
           .bind("id", id)
           .mapToBean(Teacher.class)
           .findOnly() );
   }

   @Override
   public void selectSubject(int teacherId, int subjectId) {
      jdbi.useHandle( handle -> handle.execute("INSERT INTO teacher_subject(teacher_id, subject_id) VALUES(?, ?)",
              teacherId, subjectId) );
   }

   @Override
   public boolean deleteTeacher(Teacher teacher) {
      teachers.remove(teacher);
      return false;
   }

   @Override
   public List<Lesson> getTeacherLessons(int theId) {
      String sql = "SELECT t.id t_id, t.first_name t_first_name, t.last_name t_last_name, t.email t_email, t.tokens t_tokens, " +
              "ts.id ts_id, ts.teacher_id ts_teacher_id, ts.subject_id ts_subject_id, " +
              "s.id s_id, s.subject_name s_subject_name, " +
              "l.id l_id, l.lesson_name l_lesson_name, l.subject_id l_subject_id, l.time l_time " +
              "FROM teacher t " +
              "INNER JOIN teacher_subject ts " +
              "ON ts.teacher_id = t.id " +
              "INNER JOIN subject s " +
              "ON ts.subject_id = s.id " +
              "INNER JOIN lesson l " +
              "ON l.subject_id = s.id " +
              "WHERE t.id = " + theId;

      return jdbi.withHandle( handle -> {
         teacherLessons = handle.createQuery(sql)
              .registerRowMapper(BeanMapper.factory(Teacher.class, "t"))
              .registerRowMapper(BeanMapper.factory(TeacherSubject.class, "ts"))
              .registerRowMapper(BeanMapper.factory(Subject.class, "s"))
              .registerRowMapper(BeanMapper.factory(Lesson.class, "l"))
              .reduceRows(new LinkedHashMap<Long, Lesson>(), (map, rowView) -> {
                 Lesson lesson = map.computeIfAbsent(
                         rowView.getColumn("l_id", Long.class),
                         id -> rowView.getRow(Lesson.class)
                 );

                 if (rowView.getColumn("ts_id", Long.class) != null)
                    lesson.addTeacherSubject(rowView.getRow(TeacherSubject.class));

                 if (rowView.getColumn("s_id", Long.class) != null)
                    lesson.addSubject(rowView.getRow(Subject.class));

                 if (rowView.getColumn("l_id", Long.class) != null)
                    lesson.addLesson(rowView.getRow(Lesson.class));

                 return map;

              })
              .values()
              .stream()
              .collect(toList());

         return teacherLessons;
      });
   }







}
