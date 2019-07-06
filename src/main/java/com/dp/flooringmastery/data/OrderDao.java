package com.dp.flooringmastery.data;

import com.dp.flooringmastery.models.Order;
import java.util.List;

public interface OrderDao {
    void add(Order order, String date, String folder) throws FileStorageException;
    
    List<Order> findByDate(String date, String folder) throws FileStorageException;
    
    boolean edit(Order order, String date, String folder) throws FileStorageException;
    
    boolean delete(int orderNumber, String date, String folder) throws FileStorageException;
    
}
