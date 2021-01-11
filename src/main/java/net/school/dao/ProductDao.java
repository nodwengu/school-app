package net.school.dao;

import net.school.exceptions.OutOfTokensException;
import net.school.model.*;

import java.util.List;

public interface ProductDao {
   boolean add(Product product);
   List<Product> getAll();
   boolean delete(Long productId);
   boolean update(Long productId, Product product);
   Product getById(Long productId);

   boolean addToSales(Long buyerId, String buyerName, String buyerSurname, Long productId, Long dayId);
   boolean addToBuyerHistory(Long buyerId, Long productId, Long dayId);
   List<Product> getBuyerHistory(Long buyerId);
   boolean deleteFromBuyerHistory(Long buyerId, Long productId);
   List<SoldProduct> getDailyProductsSold(Long dayId);
   List<SoldProduct> getAllProductsSold();
}
