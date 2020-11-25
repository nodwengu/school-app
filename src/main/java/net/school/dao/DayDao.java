package net.school.dao;

import net.school.model.Day;
import net.school.model.Grade;

import java.util.List;

public interface DayDao {
   List<Day> getAll();
   Day getById(Long id);
   Day getByName(String name);
}
