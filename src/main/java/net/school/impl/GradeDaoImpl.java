package net.school.impl;

import net.school.dao.GradeDao;
import net.school.model.Grade;
import org.jdbi.v3.core.Jdbi;

import java.util.List;

public class GradeDaoImpl implements GradeDao {
   private Jdbi jdbi;

   public GradeDaoImpl() {}

   public GradeDaoImpl(Jdbi jdbi) {
      this.jdbi = jdbi;
   }

   @Override
   public List<Grade> getAll() {
      return jdbi.withHandle(handle -> handle.createQuery("SELECT id, grade_name FROM grade")
         .mapToBean(Grade.class)
         .list() );
   }

   @Override
   public Grade getById(Long id) {
      return jdbi.withHandle(handle -> handle.createQuery("SELECT id, grade_name FROM grade WHERE id = :id")
              .bind("id", id)
              .mapToBean(Grade.class)
              .findOnly() );
   }
}
