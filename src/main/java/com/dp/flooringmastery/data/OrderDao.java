package com.dp.flooringmastery.data;

import com.dp.flooringmastery.models.Order;
import java.util.List;

public interface OrderDao {
    List<Order> findByDate(String date);

    void add(Order order, String date)
            throws FileStorageException;

    boolean edit(Order order, Order editedOrder, String date)
            throws FileStorageException;

    boolean delete(int orderNumber, String date)
            throws FileStorageException;
}
