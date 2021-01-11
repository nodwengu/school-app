package net.school.impl;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.school.dao.LearnerDao;
import net.school.model.*;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.BeanMapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class LearnerDaoImpl implements LearnerDao {
   private Jdbi jdbi;
   private List<Lesson> lessons = new ArrayList<>();
   private List<Learner> list = new ArrayList<>();
   private List<Learner> learnersForGrade = new ArrayList<>();
   private List<Learner> learnersForList = new ArrayList<>();

   private List<Subject> subjectlist = new ArrayList<>();

   private List<Learner> classmates = new ArrayList<>();

   public LearnerDaoImpl(){}

   public LearnerDaoImpl(Jdbi jdbi){
      this.jdbi = jdbi;
   }

   @Override
   public boolean registerLearner(Learner learner) {
      jdbi.useHandle(handle -> handle.execute("INSERT INTO learner(first_name, last_name, email, tokens, grade_id) VALUES(?,?,?,?,?)",
              learner.getFirstName(),
              learner.getLastName(),
              learner.getEmail(),
              learner.getTokens(),
              learner.getGradeId())
      );
      return true;
   }

   @Override
   public List<Learner> getAll() {
      return jdbi.withHandle(handle -> handle.createQuery("SELECT id, first_name, last_name, email, tokens FROM learner")
         .mapToBean(Learner.class)
         .list() );
   }

   @Override
   public Learner getById(Long id) {
      return jdbi.withHandle(handle -> handle.createQuery("SELECT id, first_name, last_name, email, tokens, grade_id FROM learner WHERE id = :id")
            .bind("id", id)
            .mapToBean(Learner.class)
            .findOnly() );
   }

   @Override
   public boolean update(Long id, Learner learner) {
      String sql = "UPDATE learner SET first_name=?, last_name=?, email=?, tokens=?, grade_id=? WHERE id=?";

      jdbi.useTransaction(handle -> handle.createUpdate(sql)
           .bind(0, learner.getFirstName())
           .bind(1, learner.getLastName())
           .bind(2, learner.getEmail())
           .bind(3, learner.getTokens())
           .bind(4, learner.getGradeId())
           .bind(5, learner.getId())
           .execute() );
      System.out.println("UPDATE SUCCESS...");

      return true;
   }

   @Override
   public boolean selectSubject(Long learnerId, Long subjectId) {
         jdbi.useHandle( handle -> handle.execute("INSERT INTO learner_subject(learner_id, subject_id) VALUES(?, ?)",
                 learnerId, subjectId) );
         return true;
   }

   @Override
   public boolean delete(Long learnerId) {
      jdbi.useHandle(handle -> handle.execute("DELETE FROM learner WHERE id = ?", learnerId));
      System.out.println("DELETE SUCCESS MESG...");
      return false;
   }

   @Override
   public boolean removeLearnerSubject(Long subjectId) {
      jdbi.useHandle(handle -> handle.execute("DELETE FROM learner_subject WHERE subject_id = ?",subjectId));
      return true;
   }

   @Override
   public List<Learner> getSubjects(Long learnerId) {
      String sql = "SELECT ln.id ln_id, ln.first_name ln_first_name, ln.last_name ln_last_name, ln.email ln_email, ln.tokens ln_tokens, " +
              "ls.id ls_id, ls.learner_id ls_learner_id, ls.subject_id ls_subject_id, " +
              "s.id s_id, s.subject_name s_subject_name " +
              "FROM learner ln " +
              "INNER JOIN learner_subject ls " +
              "ON ls.learner_id = ln.id " +
              "INNER JOIN subject s " +
              "ON ls.subject_id = s.id " +
              "WHERE ln.id = " + learnerId;

      return jdbi.withHandle( handle -> {
         list = handle.createQuery(sql)
              .registerRowMapper(BeanMapper.factory(Learner.class, "ln"))
              .registerRowMapper(BeanMapper.factory(LearnerSubject.class, "ls"))
              .registerRowMapper(BeanMapper.factory(Subject.class, "s"))
              .reduceRows(new LinkedHashMap<Long, Learner>(), (map, rowView) -> {
                 Learner learner = map.computeIfAbsent(
                         rowView.getColumn("ln_id", Long.class),
                         id -> rowView.getRow(Learner.class)
                 );

                 if (rowView.getColumn("ls_id", Long.class) != null)
                    learner.addLearnerSubject(rowView.getRow(LearnerSubject.class));

                 if (rowView.getColumn("s_id", Long.class) != null)
                    learner.addSubject(rowView.getRow(Subject.class));

                 return map;

              })
              .values()
              .stream()
              .collect(toList());

         return list;
      });
   }


   @Override
   public List<Subject> allSubjects(Long learnerId) {
      String sql = "SELECT s.id s_id, s.subject_name s_subject_name, " +
              "ln.id ln_id, ln.first_name ln_first_name, ln.last_name ln_last_name, ln.email ln_email, ln.tokens ln_tokens, " +
              "ls.id ls_id, ls.learner_id ls_learner_id, ls.subject_id ls_subject_id " +
              "FROM subject s " +
              "INNER JOIN learner_subject ls " +
              "ON ls.subject_id = s.id " +
              "INNER JOIN learner ln " +
              "ON ls.learner_id = ln.id " +
              "WHERE ln.id = " + learnerId;

      return jdbi.withHandle( handle -> {
         subjectlist = handle.createQuery(sql)
                 .registerRowMapper(BeanMapper.factory(Learner.class, "ln"))
                 .registerRowMapper(BeanMapper.factory(LearnerSubject.class, "ls"))
                 .registerRowMapper(BeanMapper.factory(Subject.class, "s"))

                 .reduceRows(new LinkedHashMap<Long, Subject>(), (map, rowView) -> {
                    Subject subject = map.computeIfAbsent(
                            rowView.getColumn("s_id", Long.class),
                            id -> rowView.getRow(Subject.class)
                    );

//                    if (rowView.getColumn("ls_id", Long.class) != null)
//                       learner.addLearnerSubject(rowView.getRow(LearnerSubject.class));

                    return map;

                 })
                 .values()
                 .stream()
                 .collect(toList());

         return subjectlist;
      });
   }


   @Override
   public boolean attendLesson(Long learnerId, Long lessonId) {
      jdbi.useHandle( handle -> handle.execute("INSERT INTO learner_lesson_attendant(learner_id, lesson_id) VALUES(?, ?)",
           learnerId, lessonId) );
      return true;
   }

   //getLearnersAttending

   @Override
   public List<Learner> getLearnersAttending(Long lessonId) {
      Multimap<Learner, LearnerLessonAttendant> joined = HashMultimap.create();
      String sql = "SELECT ln.id ln_id, ln.first_name ln_first_name, ln.last_name ln_last_name, ln.email ln_email, ln.tokens ln_tokens, ln.grade_id ln_grade_id, " +
              "lla.id lla_id, lla.learner_id lla_learner_id, lla.lesson_id lla_lesson_id " +
              "FROM learner ln " +
              "INNER JOIN learner_lesson_attendant lla " +
              "ON ln.id = lla.learner_id " +
              "WHERE lla.lesson_id = " + lessonId;

      return jdbi.withHandle( handle -> {
         list = handle.createQuery(sql)
                 .registerRowMapper(BeanMapper.factory(Learner.class, "ln"))
                 .registerRowMapper(BeanMapper.factory(LearnerLessonAttendant.class, "lla"))
                 .reduceRows(new LinkedHashMap<Long, Learner>(), (map, rowView) -> {
                    Learner learner = map.computeIfAbsent(
                            rowView.getColumn("ln_id", Long.class),
                            id -> rowView.getRow(Learner.class)
                    );

                    if (rowView.getColumn("lla_id", Long.class) != null)
                       learner.addLearnerFor(rowView.getRow(LearnerLessonAttendant.class));

                    return map;

                 })
                 .values()
                 .stream()
                 .collect(toList());

         return list;
      });
   }


   @Override
   public List<Lesson> getLessons(Long learnerId) {
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
              "ON ts.grade_id = g.id " +
              "INNER JOIN days d " +
              "ON l.day_id = d.id " +
              "WHERE ln.id = " + learnerId;

      return jdbi.withHandle( handle -> {
         lessons = handle.createQuery(sql)
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

         return lessons;
      });
   }


   public List<Lesson> getLessonsByDay(Long learnerId, Long dayId) {
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
              "ON ts.grade_id = g.id " +
              "INNER JOIN days d " +
              "ON l.day_id = d.id " +
              "WHERE ln.id = " + learnerId + " AND d.id = " + dayId +
              "ORDER BY l.time ASC";

      return jdbi.withHandle( handle -> {
         lessons = handle.createQuery(sql)
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

         return lessons;
      });
   }



   @Override
   public List<Learner> getLearnersForGrade(Long gradeId) {
      Multimap<Learner, Grade> joined = HashMultimap.create();
      String sql = "SELECT ln.id ln_id, ln.first_name ln_first_name, ln.last_name ln_last_name, ln.email ln_email, ln.tokens ln_tokens, ln.grade_id ln_grade_id, " +
              "g.id g_id, g.grade_name g_grade_name " +
              "FROM learner ln " +
              "INNER JOIN grade g " +
              "ON ln.grade_id = g.id " +
              "WHERE g.id = " + gradeId;

      return jdbi.withHandle( handle -> {
         learnersForGrade = handle.createQuery(sql)
                 .registerRowMapper(BeanMapper.factory(Learner.class, "ln"))
                 .registerRowMapper(BeanMapper.factory(Grade.class, "g"))
                 .reduceRows(new LinkedHashMap<Long, Learner>(), (map, rowView) -> {
                    Learner learner = map.computeIfAbsent(
                            rowView.getColumn("ln_id", Long.class),
                            id -> rowView.getRow(Learner.class)
                    );

                    if (rowView.getColumn("g_id", Long.class) != null)
                       learner.addGrade(rowView.getRow(Grade.class));

                    return map;

                 })
                 .values()
                 .stream()
                 .collect(toList());

         return learnersForGrade;
      });
   }

   @Override
   public List<Learner> getClassmates(Long gradeId) {
      String sql = "SELECT ln.id ln_id, ln.first_name ln_first_name, ln.last_name ln_last_name, ln.email ln_email, ln.grade_id ln_grade_id, " +
              "ls.id ls_id, ls.learner_id ls_learner_id, ls.subject_id ls_subject_id, " +
              "s.id s_id, s.subject_name s_subject_name " +
              "FROM learner ln " +
              "INNER JOIN learner_subject ls " +
              "ON ln.id = ls.learner_id " +
              "INNER JOIN subject s " +
              "ON ls.subject_id = s.id " +
              "WHERE ln.grade_id = " + gradeId;

      return jdbi.withHandle( handle -> {
         classmates = handle.createQuery(sql)
                 .registerRowMapper(BeanMapper.factory(LearnerSubject.class, "ls"))
                 .registerRowMapper(BeanMapper.factory(Subject.class, "s"))
                 .registerRowMapper(BeanMapper.factory(Learner.class, "ln"))
                 .reduceRows(new LinkedHashMap<Long, Learner>(), (map, rowView) -> {
                    Learner learner = map.computeIfAbsent(
                            rowView.getColumn("ln_id", Long.class),
                            id -> rowView.getRow(Learner.class)
                    );

                    if (rowView.getColumn("s_id", Long.class) != null)
                       learner.addSubject(rowView.getRow(Subject.class));

                    if (rowView.getColumn("ls_id", Long.class) != null)
                       learner.addLearnerSubject(rowView.getRow(LearnerSubject.class));

                    return map;

                 })
                 .values()
                 .stream()
                 .collect(toList());

         return classmates;
      });
   }

   @Override
   public List<Lesson> getClassmateLessons(Long learner_id) {

      return null;
   }

}
