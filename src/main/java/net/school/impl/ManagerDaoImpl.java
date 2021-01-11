package net.school.impl;

import net.school.dao.ManagerDao;
import net.school.model.Manager;
import org.jdbi.v3.core.Jdbi;

import java.util.List;

public class ManagerDaoImpl implements ManagerDao {
   private Jdbi jdbi;
   private int totalDailyTokens;

   public ManagerDaoImpl(Jdbi jdbi) {
      this.jdbi = jdbi;
   }

   @Override
   public boolean add(Manager manager) {
      jdbi.useHandle(handle -> handle.execute("INSERT INTO manager(first_name, last_name, email) VALUES(?,?,?)",
              manager.getFirstName(), manager.getLastName(), manager.getEmail())
      );
      return true;
   }

   @Override
   public List<Manager> getAll() {
      return jdbi.withHandle(handle -> handle.createQuery("SELECT id, first_name, last_name, email FROM manager")
         .mapToBean(Manager.class)
         .list() );
   }

   @Override
   public boolean update(Long managerId, Manager manager) {
      String sql = "UPDATE manager SET first_name=?, last_name=?, email=? WHERE id=?";

      jdbi.useTransaction(handle -> handle.createUpdate(sql)
         .bind(0, manager.getFirstName())
         .bind(1, manager.getFirstName())
         .bind(2, manager.getEmail())
         .bind(3, managerId)
         .execute() );

      return true;
   }

   @Override
   public Manager getById(Long managerId) {
      return jdbi.withHandle(handle -> handle.createQuery("SELECT id, first_name, last_name, email FROM manager WHERE id=? ")
         .bind(0, managerId)
         .mapToBean(Manager.class)
         .findOnly() );
   }

   @Override
   public boolean delete(Long managerId) {
      jdbi.useHandle(handle -> handle.execute("DELETE FROM manager WHERE id=?", managerId));
      return true;
   }

   @Override
   public int getDailyTokens(Long dayId) {
      String sql = "SELECT SUM(cost) " +
              "FROM product p " +
              "INNER JOIN sold_product sp " +
              "ON p.id = sp.product_id " +
              "INNER JOIN days d " +
              "ON d.id = sp.day_id " +
              "WHERE d.id = " + dayId;

      return jdbi.withHandle(handle -> handle.createQuery(sql)
         .mapTo(Integer.class)
         .findOnly()
      );

   }
}
