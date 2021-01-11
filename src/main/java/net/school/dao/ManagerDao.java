package net.school.dao;

import net.school.model.Manager;

import java.util.List;

public interface ManagerDao {
   boolean add(Manager manager);
   List<Manager> getAll();
   boolean delete(Long managerId);
   boolean update(Long managerId, Manager manager);
   Manager getById(Long managerId);

   int getDailyTokens(Long dayId);

}
