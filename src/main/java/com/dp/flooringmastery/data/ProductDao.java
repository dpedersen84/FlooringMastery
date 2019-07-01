package com.dp.flooringmastery.data;

import com.dp.flooringmastery.models.Product;
import java.util.List;

public interface ProductDao {
    
    Product getProduct();
    
    List<Product> getAllProducts();
}
