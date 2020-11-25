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
   private List<Learner> learnersForList = new ArrayList<>();

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
      return jdbi.withHandle(handle -> handle.createQuery("SELECT id, first_name, last_name, email, tokens FROM learner WHERE id = :id")
            .bind("id", id)
            .mapToBean(Learner.class)
            .findOnly() );
   }

   @Override
   public void selectSubject(Long learnerId, Long subjectId) {
         jdbi.useHandle( handle -> handle.execute("INSERT INTO learner_subject(learner_id, subject_id) VALUES(?, ?)",
                 learnerId, subjectId) );
   }

   @Override
   public boolean delete(Learner learner) {
      return false;
   }

   @Override
   public List<Lesson> getAllLessons(Long theId) {
      String sql = "SELECT ln.id ln_id, ln.first_name ln_first_name, ln.last_name ln_last_name, ln.email ln_email, ln.tokens ln_tokens, " +
              "ls.id ls_id, ls.learner_id ls_learner_id, ls.subject_id ls_subject_id, " +
              "s.id s_id, s.subject_name s_subject_name, " +
              "l.id l_id, l.lesson_name l_lesson_name, l.subject_id l_subject_id, l.time l_time, " +
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
              "WHERE ln.id = " + theId;

      return jdbi.withHandle( handle -> {
         lessons = handle.createQuery(sql)
                 .registerRowMapper(BeanMapper.factory(Learner.class, "ln"))
                 .registerRowMapper(BeanMapper.factory(LearnerSubject.class, "ls"))
                 .registerRowMapper(BeanMapper.factory(Subject.class, "s"))
                 .registerRowMapper(BeanMapper.factory(Teacher.class, "t"))
                 .registerRowMapper(BeanMapper.factory(Lesson.class, "l"))
                 .reduceRows(new LinkedHashMap<Long, Lesson>(), (map, rowView) -> {
                    Lesson lesson = map.computeIfAbsent(
                            rowView.getColumn("l_id", Long.class),
                            id -> rowView.getRow(Lesson.class)
                    );

                    if (rowView.getColumn("ls_id", Long.class) != null)
                       lesson.addLearnerSubject(rowView.getRow(Grade10learnerSubject.class));

                    if (rowView.getColumn("s_id", Long.class) != null) {
                       //System.out.println(!lesson.getSubjects().contains(rowView.getRow(Subject.class)));
                       if (lesson.getSubjects().contains(rowView.getRow(Subject.class))) {
                          System.out.println("ALREADY EXITS");
                       } else {
                          lesson.addSubject(rowView.getRow(Subject.class));
                       }
//
                    }

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
   public List<Learner> getAllSubjects(Long myId) {
      String sql = "SELECT ln.id ln_id, ln.first_name ln_first_name, ln.last_name ln_last_name, ln.email ln_email, ln.tokens ln_tokens, " +
              "ls.id ls_id, ls.learner_id ls_learner_id, ls.subject_id ls_subject_id, " +
              "s.id s_id, s.subject_name s_subject_name " +
              "FROM learner ln " +
              "INNER JOIN learner_subject ls " +
              "ON ls.learner_id = ln.id " +
              "INNER JOIN subject s " +
              "ON ls.subject_id = s.id " +
              "WHERE ln.id = " + myId;

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
   public boolean attendLesson(Long learnerId, Long lessonId) {
      jdbi.useHandle( handle -> handle.execute("INSERT INTO learner_lesson_attendant(learner_id, lesson_id) VALUES(?, ?)",
           learnerId, lessonId) );
      return true;
   }

   @Override
   public List<Learner> getLearnersFor(Long lessonId) {
      Multimap<Learner, LearnerLessonAttendant> joined = HashMultimap.create();
      String sql = "SELECT ln.id ln_id, ln.first_name ln_first_name, ln.last_name ln_last_name, ln.email ln_email, ln.tokens ln_tokens, " +
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
   public List<LearnerSubject> getLearnerSubjects(Long myId) {
      return null;
   }
}
