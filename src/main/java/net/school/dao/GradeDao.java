package net.school.dao;

import net.school.model.Grade;

import java.util.List;

public interface GradeDao {
   List<Grade> getAll();
   Grade getById(Long id);
}
