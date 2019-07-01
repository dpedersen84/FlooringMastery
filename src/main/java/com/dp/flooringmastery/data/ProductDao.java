package com.dp.flooringmastery.data;

import com.dp.flooringmastery.models.Product;
import java.util.List;

public interface ProductDao {
    
    Product getProduct(String name);
    
    List<Product> getAllProducts();
}
