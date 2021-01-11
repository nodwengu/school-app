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
   private List<Lesson> lessonsBySubjectAndDay = new ArrayList<>();
   private List<Lesson> cancelledLessons = new ArrayList<>();
   private List<Lesson> userLesons = new ArrayList<>();

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
      return jdbi.withHandle(handle -> handle.createQuery("SELECT id, subject_id, lesson_name, time, grade_id, day_id FROM lesson WHERE id=:id")
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


   @Override
   public List<Lesson> getBySubjectAndDay(Long subjectId, Long dayId) {
      String sql = "SELECT ls.id ls_id, ls.lesson_name ls_lesson_name, ls.subject_id ls_subject_id, ls.day_id ls_day_id, " +
              "s.id s_id, s.subject_name s_subject_name, " +
              "g.id g_id, g.grade_name g_grade_name, " +
              "d.id d_id, d.day_name d_day_name " +
              "FROM lesson ls " +
              "INNER JOIN subject s " +
              "ON ls.subject_id = s.id " +
              "INNER JOIN days d " +
              "ON ls.day_id = d.id " +
              "INNER JOIN grade g " +
              "ON ls.grade_id = g.id " +
              "WHERE s.id = " + subjectId + " AND d.id = " + dayId;

      return jdbi.withHandle( handle -> {
         lessonsBySubjectAndDay = handle.createQuery(sql)
                 .registerRowMapper(BeanMapper.factory(Subject.class, "s"))
                 .registerRowMapper(BeanMapper.factory(Day.class, "d"))
                 .registerRowMapper(BeanMapper.factory(Grade.class, "g"))
                 .registerRowMapper(BeanMapper.factory(Lesson.class, "ls"))
                 .reduceRows(new LinkedHashMap<Long, Lesson>(), (map, rowView) -> {
                    Lesson lesson = map.computeIfAbsent(
                            rowView.getColumn("ls_id", Long.class),
                            id -> rowView.getRow(Lesson.class)
                    );

                    if (rowView.getColumn("s_id", Long.class) != null)
                       lesson.addSubject(rowView.getRow(Subject.class));

                    if (rowView.getColumn("d_id", Long.class) != null)
                       lesson.addDay(rowView.getRow(Day.class));

                    if (rowView.getColumn("g_id", Long.class) != null)
                       lesson.addGrade(rowView.getRow(Grade.class));

                    return map;

                 })
                 .values()
                 .stream()
                 .collect(toList());

         return lessonsBySubjectAndDay;
      });
   }


   @Override
   public boolean saveCancelledLesson(Long lessonId) {
      jdbi.useHandle(handle -> handle.execute("INSERT INTO cancelled_lessons(lesson_id) VALUES(?)", lessonId));
      return true;
   }


   @Override
   public List<Lesson> getCancelledLesson() {
      String sql = "SELECT ls.id ls_id, ls.lesson_name ls_lesson_name, ls.subject_id ls_subject_id, ls.day_id ls_day_id, " +
              "s.id s_id, s.subject_name s_subject_name, " +
              "g.id g_id, g.grade_name g_grade_name, " +
              "d.id d_id, d.day_name d_day_name " +
              "FROM lesson ls " +
              "INNER JOIN cancelled_lessons cl " +
              "ON ls.id = cl.lesson_id " +
              "INNER JOIN subject s " +
              "ON ls.subject_id = s.id " +
              "INNER JOIN days d " +
              "ON ls.day_id = d.id " +
              "INNER JOIN grade g " +
              "ON ls.grade_id = g.id ";

      return jdbi.withHandle( handle -> {
         cancelledLessons = handle.createQuery(sql)
                 .registerRowMapper(BeanMapper.factory(Subject.class, "s"))
                 .registerRowMapper(BeanMapper.factory(Day.class, "d"))
                 .registerRowMapper(BeanMapper.factory(Grade.class, "g"))
                 .registerRowMapper(BeanMapper.factory(Lesson.class, "ls"))
                 .reduceRows(new LinkedHashMap<Long, Lesson>(), (map, rowView) -> {
                    Lesson lesson = map.computeIfAbsent(
                            rowView.getColumn("ls_id", Long.class),
                            id -> rowView.getRow(Lesson.class)
                    );

                    if (rowView.getColumn("s_id", Long.class) != null)
                       lesson.addSubject(rowView.getRow(Subject.class));

                    if (rowView.getColumn("d_id", Long.class) != null)
                       lesson.addDay(rowView.getRow(Day.class));

                    if (rowView.getColumn("g_id", Long.class) != null)
                       lesson.addGrade(rowView.getRow(Grade.class));

                    return map;

                 })
                 .values()
                 .stream()
                 .collect(toList());

         return cancelledLessons;
      });
   }

   @Override
   public List<Lesson> getUserLessonsByGradeAndDay(Long userId, Long gradeId, Long dayId) {
      String sql = "SELECT ln.id ln_id, ln.first_name ln_first_name, ln.last_name ln_last_name, ln.email ln_email, ln.tokens ln_tokens, " +
              "ls.id ls_id, ls.learner_id ls_learner_id, ls.subject_id ls_subject_id, " +
              "s.id s_id, s.subject_name s_subject_name, " +
              "d.id d_id, d.day_name d_day_name, " +
              "g.id g_id, g.grade_name g_grade_name, " +
              "ts.id ts_id, ts.teacher_id ts_teacher_id, ts.subject_id ts_subject_id, ts.grade_id ts_grade_id, " +
              "l.id l_id, l.lesson_name l_lesson_name, l.subject_id l_subject_id, l.time l_time, l.day_id l_day_id, l.grade_id l_grade_id, " +
              "t.id t_id, t.first_name t_first_name, t.last_name t_last_name, t.email t_email, t.tokens t_tokens " +

              "FROM learner ln " +
              "INNER JOIN learner_subject ls " +
              "ON ls.learner_id = ln.id " +
              "INNER JOIN subject s " +
              "ON ls.subject_id = s.id " +
              "INNER JOIN lesson l " +
              "ON l.subject_id = s.id " +
              "INNER JOIN teacher_subject ts " +
              "ON s.id = ts.subject_id " +
              "INNER JOIN teacher t " +
              "ON ts.teacher_id = t.id " +
              "INNER JOIN grade g " +
              "ON l.grade_id = g.id " +
              "INNER JOIN days d " +
              "ON l.day_id = d.id " +
              "WHERE ln.id = " + userId + " AND d.id = " + dayId + " AND l.grade_id = " + gradeId +
              "ORDER BY l.time ASC";

      return jdbi.withHandle( handle -> {
         userLesons = handle.createQuery(sql)
                 .registerRowMapper(BeanMapper.factory(Learner.class, "ln"))
                 .registerRowMapper(BeanMapper.factory(LearnerSubject.class, "ls"))
                 .registerRowMapper(BeanMapper.factory(Subject.class, "s"))
                 .registerRowMapper(BeanMapper.factory(Teacher.class, "t"))
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

                    if (rowView.getColumn("s_id", Long.class) != null)
                       lesson.addSubject(rowView.getRow(Subject.class));

                    if (rowView.getColumn("l_id", Long.class) != null)
                       lesson.addLesson(rowView.getRow(Lesson.class));

                    if (rowView.getColumn("t_id", Long.class) != null) {
                       lesson.addTeacher(rowView.getRow(Teacher.class));
                    }

                    return map;

                 })
                 .values()
                 .stream()
                 .collect(toList());

         return userLesons;
      });
   }


   @Override
   public Lesson getByNotesId(Long id) {
      String sql = "SELECT lesson_name, subject_id, time, day_id, grade_id FROM lesson WHERE id=?";
      return jdbi.withHandle(handle -> handle.createQuery(sql)
            .bind(0, id)
            .mapToBean(Lesson.class)
            .findOnly() );
   }
}
