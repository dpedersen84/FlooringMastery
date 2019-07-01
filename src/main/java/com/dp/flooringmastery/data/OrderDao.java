package com.dp.flooringmastery.data;

import com.dp.flooringmastery.models.Order;
import java.util.List;

public interface OrderDao {
    
    void add(Order order);
    
    List<Order> findAll();
    
    List<Order> findByDate();
    
    Order findById();
    
    boolean edit(Order order);
    
    boolean removeById(int id);
    
}
