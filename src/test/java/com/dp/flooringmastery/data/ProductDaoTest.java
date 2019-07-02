package com.dp.flooringmastery.data;

import com.dp.flooringmastery.models.Product;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProductDaoTest {
    private ProductDao dao = new ProductFileDao("products-test.txt");
    
    public ProductDaoTest() {
    }

    /**
     * Test of getProduct method, of class ProductDao.
     */
    @Test
    public void testGetProduct() {
        String testString = "Plank";
        Product testProduct = dao.getProduct(testString);
        
        assertEquals(testString, testProduct.getName());
    }

    /**
     * Test of getAllProducts method, of class ProductDao.
     */
    @Test
    public void testGetAllProducts() throws FileStorageException {
        List<Product> products = dao.getAllProducts();
        
        assertEquals(15, products.size());
    }
    
}
