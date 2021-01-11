package net.school.impl;

import net.school.dao.LearnerDao;
import net.school.dao.ProductDao;
import net.school.dao.TeacherDao;
import net.school.exceptions.OutOfTokensException;
import net.school.model.*;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.BeanMapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class ProductDaoImpl implements ProductDao {
   private Jdbi jdbi;
   private List<Product> buyerItemsHistory = new ArrayList<>();
   private List<SoldProduct> dailySales = new ArrayList<>();
   private List<SoldProduct> allSales = new ArrayList<>();

   public ProductDaoImpl(Jdbi jdbi) {
      this.jdbi = jdbi;
   }

   @Override
   public boolean add(Product product) {
      jdbi.useHandle(h -> h.execute("INSERT INTO product(product_name, cost) VALUES(?,?)",
              product.getProductName(), product.getCost()) );

      return true;
   }

   @Override
   public List<Product> getAll() {
      return jdbi.withHandle(h -> h.createQuery("SELECT id, product_name, cost FROM product")
            .mapToBean(Product.class)
            .list() );
   }

   @Override
   public Product getById(Long productId) {
      return jdbi.withHandle(handle -> handle.createQuery("SELECT id, product_name, cost FROM product WHERE id=?")
         .bind(0, productId)
         .mapToBean(Product.class)
         .findOnly() );
   }

   @Override
   public boolean update(Long productId, Product product) {
      String sql = "UPDATE product SET product_name=?, cost=? WHERE id=? ";
      jdbi.useTransaction(handle -> handle.createUpdate(sql)
         .bind(0, product.getProductName())
         .bind(1, product.getCost())
         .bind(2, productId)
         .execute() );

      return true;
   }

   @Override
   public boolean delete(Long productId) {
      jdbi.useHandle(handle -> handle.execute("DELETE FROM product WHERE id=?", productId ));

      return true;
   }


   @Override
   public boolean addToSales(Long buyerId, String buyerName, String buyerSurname, Long productId, Long dayId) {
      jdbi.useHandle(handle -> handle.execute("INSERT INTO sold_product(buyer_id, buyer_name, buyer_surname, product_id, day_id) VALUES(?,?,?,?,?)",
              buyerId, buyerName, buyerSurname, productId, dayId));
      return true;
   }

   @Override
   public List<Product> getBuyerHistory(Long buyerId) {
      String sql = "SELECT p.id p_id, p.product_name p_product_name, p.cost p_cost, " +
              "bh.id bh_id, bh.buyer_id bh_buyer_id, bh.product_id bh_product_id, " +
              "d.id d_id, d.day_name d_day_name " +
              "FROM product p " +
              "JOIN buyer_history bh " +
              "ON p.id = bh.product_id " +
              "JOIN days d " +
              "ON bh.day_id = d.id " +
              "WHERE bh.buyer_id = " + buyerId;

      return jdbi.withHandle(handle -> {
         buyerItemsHistory = handle.createQuery(sql)
                 //.registerRowMapper(BeanMapper.factory(BuyerProduct.class, "bp"))
                 .registerRowMapper(BeanMapper.factory(Day.class, "d"))
                 .registerRowMapper(BeanMapper.factory(Product.class, "p"))
                 .reduceRows(new LinkedHashMap<Long, Product>(), (map, rowView) -> {
                    Product product = map.computeIfAbsent(rowView.getColumn("p_id", Long.class),
                            id -> rowView.getRow(Product.class));

                    if (rowView.getColumn("d_id", Long.class) != null)
                       product.addDay(rowView.getRow(Day.class));

                    return map;

                 })
                 .values()
                 .stream()
                 .collect(toList());
         return buyerItemsHistory;
      });
   }


   @Override
   public List<SoldProduct> getDailyProductsSold(Long dayId) {
      String sql = "SELECT p.id p_id, p.product_name p_product_name, p.cost p_cost, " +
              "sp.id sp_id, sp.buyer_id sp_buyer_id, sp.product_id sp_product_id, sp.day_id sp_day_id, sp.buyer_name sp_buyer_name, sp.buyer_surname sp_buyer_surname, " +
              "d.id d_id, d.day_name d_day_name " +

              "FROM sold_product sp " +
              "JOIN product p " +
              "ON p.id = sp.product_id " +
              "JOIN days d " +
              "ON sp.day_id = d.id " +
              "WHERE sp.day_id = " + dayId;

      return jdbi.withHandle(handle -> {
         dailySales = handle.createQuery(sql)
                 .registerRowMapper(BeanMapper.factory(SoldProduct.class, "sp"))
                 .registerRowMapper(BeanMapper.factory(Day.class, "d"))
                 .registerRowMapper(BeanMapper.factory(Product.class, "p"))

                 .reduceRows(new LinkedHashMap<Long, SoldProduct>(), (map, rowView) -> {
                    SoldProduct soldProduct = map.computeIfAbsent(rowView.getColumn("sp_id", Long.class),
                            id -> rowView.getRow(SoldProduct.class));

                    if (rowView.getColumn("d_id", Long.class) != null)
                       soldProduct.addDay(rowView.getRow(Day.class));

                    if (rowView.getColumn("sp_id", Long.class) != null)
                       soldProduct.addProduct(rowView.getRow(Product.class));

                    return map;

                 })
                 .values()
                 .stream()
                 .collect(toList());
         return dailySales;
      });


   }


   @Override
   public List<SoldProduct> getAllProductsSold() {
      String sql = "SELECT p.id p_id, p.product_name p_product_name, p.cost p_cost, " +
              "sp.id sp_id, sp.buyer_id sp_buyer_id, sp.product_id sp_product_id, sp.day_id sp_day_id, sp.buyer_name sp_buyer_name, sp.buyer_surname sp_buyer_surname, " +
              "d.id d_id, d.day_name d_day_name " +

              "FROM sold_product sp " +
              "JOIN product p " +
              "ON p.id = sp.product_id " +
              "JOIN days d " +
              "ON sp.day_id = d.id ";
//              "WHERE sp.day_id = " + dayId;

      return jdbi.withHandle(handle -> {
         allSales = handle.createQuery(sql)
                 .registerRowMapper(BeanMapper.factory(SoldProduct.class, "sp"))
                 .registerRowMapper(BeanMapper.factory(Day.class, "d"))
                 .registerRowMapper(BeanMapper.factory(Product.class, "p"))

                 .reduceRows(new LinkedHashMap<Long, SoldProduct>(), (map, rowView) -> {
                    SoldProduct soldProduct = map.computeIfAbsent(rowView.getColumn("sp_id", Long.class),
                            id -> rowView.getRow(SoldProduct.class));

                    if (rowView.getColumn("d_id", Long.class) != null)
                       soldProduct.addDay(rowView.getRow(Day.class));

                    if (rowView.getColumn("sp_id", Long.class) != null)
                       soldProduct.addProduct(rowView.getRow(Product.class));

                    return map;

                 })
                 .values()
                 .stream()
                 .collect(toList());
         return allSales;
      });
   }

   @Override
   public boolean addToBuyerHistory(Long buyerId, Long productId, Long dayId) {
      jdbi.useHandle(handle -> handle.execute("INSERT INTO buyer_history(buyer_id, product_id, day_id) VALUES(?,?,?)",
              buyerId, productId, dayId));
      return true;
   }

   @Override
   public boolean deleteFromBuyerHistory(Long buyerId, Long productId) {
      jdbi.useHandle(handle -> handle.execute("DELETE FROM buyer_history WHERE buyer_id = ? AND product_id = ?",
              buyerId, productId));
      return true;
   }
}
