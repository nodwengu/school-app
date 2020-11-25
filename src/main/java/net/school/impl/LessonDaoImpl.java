package net.school.impl;

import net.school.dao.LessonDao;
import net.school.model.Lesson;
import net.school.model.Subject;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.JoinRow;
import org.jdbi.v3.core.mapper.JoinRowMapper;
import org.jdbi.v3.core.mapper.reflect.BeanMapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class LessonDaoImpl implements LessonDao {
   private Jdbi jdbi;

   public LessonDaoImpl() {}

   public LessonDaoImpl(Jdbi jdbi) {
      this.jdbi = jdbi;
   }

   @Override
   public List<Lesson> getAll() {
      return jdbi.withHandle(handle -> handle.createQuery("SELECT id, subject_id, lesson_name, time FROM lesson")
      .mapToBean(Lesson.class)
      .list() );
   }

   @Override
   public String addLesson(Lesson lesson) {
      jdbi.useHandle(handle -> handle.execute("INSERT INTO lesson(subject_id, lesson_name, time) VALUES(?,?,?)",
              lesson.getSubjectId(),
              lesson.getLessonName(),
              lesson.getTime())
      );
      return "Lesson added!";
   }

   @Override
   public Lesson getById(Long id) {
      return jdbi.withHandle(handle -> handle.createQuery("SELECT id, subject_id, lesson_name, time FROM lesson WHERE id=:id")
         .bind("id", id)
         .mapToBean(Lesson.class)
         .findOnly() );
   }

   @Override
   public boolean deleteLesson(Lesson lesson) {
      return false;
   }

   @Override
   public List<JoinRow> getLessonForSubject() {
      String sql = "SELECT s.id s_id, s.subject_name s_subject_name, l.id l_id, l.lesson_name l_lesson_name, l.time l_time, l.subject_id l_subject_id " +
              "FROM subject s " +
              "INNER JOIN lesson l " +
              "ON s.id = l.subject_id";

      return  jdbi.withHandle( handle -> {
         handle.registerRowMapper(BeanMapper.factory(Subject.class, "s"));
         handle.registerRowMapper(BeanMapper.factory(Lesson.class, "l"));
         handle.registerRowMapper(JoinRowMapper.forTypes(Subject.class, Lesson.class));

         List<JoinRow> list = handle.select(sql)
                 .mapTo(JoinRow.class)
                 .list();

         return list;
      });
   }

   private List<Lesson> lessons = new ArrayList<>();
   @Override
   public List<Lesson> getTest() {
      String sql = "SELECT l.id l_id, l.lesson_name l_lesson_name, l.time l_time, l.subject_id l_subject_id, s.id s_id, s.subject_name s_subject_name " +
              "FROM lesson l " +
              "INNER JOIN subject s " +
              "ON s.id = l.subject_id";

      return jdbi.withHandle( handle -> {
         lessons = handle.createQuery(sql)
              .registerRowMapper(BeanMapper.factory(Subject.class, "s"))
              .registerRowMapper(BeanMapper.factory(Lesson.class, "l"))
              .reduceRows(new LinkedHashMap<Long, Lesson>(), (map, rowView) -> {
                 Lesson lesson = map.computeIfAbsent( rowView.getColumn("l_id", Long.class), id -> rowView.getRow(Lesson.class) );

                 if (rowView.getColumn("s_id", Long.class) != null)
                    lesson.addSubject(rowView.getRow(Subject.class));

                 return map;

              })
              .values()
              .stream()
              .collect(toList());
         return lessons;
      });
   }
}
