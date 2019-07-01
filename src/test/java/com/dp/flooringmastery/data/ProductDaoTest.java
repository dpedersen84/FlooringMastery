package com.dp.flooringmastery.data;

import com.dp.flooringmastery.models.Product;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProductDaoTest {
    private ProductDao dao = new ProductFileDao("products-test.txt");
    
    public ProductDaoTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
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
