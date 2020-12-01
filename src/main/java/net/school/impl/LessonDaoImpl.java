package net.school.impl;

import net.school.dao.LessonDao;
import net.school.model.*;
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
   private List<Lesson> lessonsByGradeAndDay = new ArrayList<>();

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
      jdbi.useHandle(handle -> handle.execute("INSERT INTO lesson(subject_id, lesson_name, time, grade_id, day_id) VALUES(?,?,?,?,?)",
              lesson.getSubjectId(),
              lesson.getLessonName(),
              lesson.getTime(),
              lesson.getGradeId(),
              lesson.getDayId())
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
   public boolean delete(Long lessonId) {
      jdbi.useHandle(handle -> handle.execute("DELETE FROM lesson WHERE id = ?", lessonId));
      return true;
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

   @Override
   public List<Lesson> getLessonsByGradeAndDay(Long gradeId, Long dayId) {
      String sql = "SELECT l.id l_id, l.lesson_name l_lesson_name, l.subject_id l_subject_id, l.time l_time, l.day_id l_day_id, l.grade_id l_grade_id, " +
              "g.id g_id, g.grade_name g_grade_name, " +
              "d.id d_id, d.day_name d_day_name " +
              "FROM lesson l " +
              "INNER JOIN grade g " +
              "ON l.grade_id = g.id " +
              "INNER JOIN days d " +
              "ON l.day_id = d.id " +
              "WHERE g.id = " + gradeId + " AND d.id = " + dayId;

      return jdbi.withHandle( handle -> {
         lessonsByGradeAndDay = handle.createQuery(sql)
                 .registerRowMapper(BeanMapper.factory(Grade.class, "g"))
                 .registerRowMapper(BeanMapper.factory(Day.class, "d"))
                 .registerRowMapper(BeanMapper.factory(Lesson.class, "l"))
                 .reduceRows(new LinkedHashMap<Long, Lesson>(), (map, rowView) -> {
                    Lesson lesson = map.computeIfAbsent(
                            rowView.getColumn("l_id", Long.class),
                            id -> rowView.getRow(Lesson.class)
                    );

                    if (rowView.getColumn("g_id", Long.class) != null)
                       lesson.addGrade(rowView.getRow(Grade.class));

                    if (rowView.getColumn("d_id", Long.class) != null)
                       lesson.addDay(rowView.getRow(Day.class));

                    return map;

                 })
                 .values()
                 .stream()
                 .collect(toList());

         return lessonsByGradeAndDay;
      });



   }





}
