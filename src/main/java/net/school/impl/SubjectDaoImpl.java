package net.school.impl;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.school.dao.SubjectDao;
import net.school.model.Learner;
import net.school.model.LearnerSubject;
import net.school.model.Lesson;
import net.school.model.Subject;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.JoinRowMapper;
import org.jdbi.v3.core.mapper.reflect.BeanMapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class SubjectDaoImpl implements SubjectDao {
   private Jdbi jdbi;
   private List<Subject> subjects = new ArrayList<>();
   private List<Subject> userSubjects = new ArrayList<>();

   public SubjectDaoImpl() {}

   public SubjectDaoImpl(Jdbi jdbi){
      this.jdbi = jdbi;
   }

   @Override
   public List<Subject> getAll() {
      return jdbi.withHandle(handle -> handle.createQuery("SELECT id, subject_name FROM subject")
         .mapToBean(Subject.class)
         .list() );
   }

//   @Override
//   public List<Subject> getAllGrade10() {
//      return jdbi.withHandle(handle -> handle.createQuery("SELECT id, subject_name FROM grade10learner_subject")
//              .mapToBean(Subject.class)
//              .list() );
//   }

   @Override
   public boolean addSubject(Subject subject) {
      jdbi.useHandle(handle -> handle.execute("INSERT INTO subject(subject_name) VALUES(?)",
              subject.getSubjectName()) );
      System.out.println("SUBJECT ADDED SUCCESSFULLY...");
      return true;
   }

   @Override
   public boolean delete(Long id) {
      jdbi.useHandle(handle -> handle.execute("DELETE FROM subject WHERE id = ?",id));
      return true;
   }

   @Override
   public Subject getById(Long id) {
      return jdbi.withHandle(handle -> handle.createQuery("SELECT id, subject_name FROM subject WHERE id = :id")
         .bind("id", id)
         .mapToBean(Subject.class)
         .findOnly()
      );
   }

   @Override
   public Long getId(String name) {
      return jdbi.withHandle(handle -> handle.createQuery("select id from subject where subject_name = ?")
           .bind(0, name)
           .mapTo(Long.class)
           .findOnly()
      );
   }

   @Override
   public boolean update(Subject subject) {
      jdbi.useTransaction(handle -> handle.createUpdate("UPDATE subject SET subject_name=? WHERE id=?")
         .bind(0, subject.getSubjectName())
         .bind(1, subject.getId())
         .execute());
      System.out.println("UPDATING SUBJECT");

      return true;
   }

   @Override
   public Multimap<Lesson, Subject> getSubjectForLesson(Long id) {
      Multimap<Lesson, Subject> joined = HashMultimap.create();

      String sql = "SELECT s.id s_id, s.subject_name s_subject_name, " +
              "l.id l_id, l.lesson_name l_name, l.time l_time, l.subject_id l_subject_id " +
              "FROM subject s " +
              "INNER JOIN lesson l " +
              "ON s.id = l.subject_id " +
              "WHERE l.id = " + id;

      return  jdbi.withHandle( handle -> {
         handle.registerRowMapper(BeanMapper.factory(Lesson.class, "l"));
         handle.registerRowMapper(BeanMapper.factory(Subject.class, "s"));

         handle.createQuery(sql)
                 .map(JoinRowMapper.forTypes(Lesson.class, Subject.class))
                 .forEach(jr -> joined.put(jr.get(Lesson.class), jr.get(Subject.class)));

         return joined;
      });
   }
}
