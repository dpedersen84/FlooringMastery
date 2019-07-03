package com.dp.flooringmastery.service;

import com.dp.flooringmastery.data.OrderDao;
import com.dp.flooringmastery.data.OrderFileDao;
import com.dp.flooringmastery.data.ProductDao;
import com.dp.flooringmastery.data.ProductFileDao;
import com.dp.flooringmastery.data.TaxRateDao;
import com.dp.flooringmastery.data.TaxRateFileDao;
import com.dp.flooringmastery.models.Order;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class OrderServiceTest {

    String path = "orders-test/";

    private static final String SEED_FILE = "orders-test/test-seed.txt";
    private static final String DATA_FILE = "orders-test/Orders_04012019.txt";
    private static final String DATA_FILE_TWO = "orders-test/Orders_04032019.txt";

    OrderDao dao = new OrderFileDao(path);

    TaxRateDao taxRateDao = new TaxRateFileDao("Taxes.txt");
    ProductDao productDao = new ProductFileDao("Products.txt");
    
    OrderService service = new OrderService(dao);

    @BeforeAll
    public static void init() throws IOException {
        Path source = Paths.get(SEED_FILE);
        Path destination = Paths.get(DATA_FILE);
        Path destinationTwo = Paths.get(DATA_FILE_TWO);
        Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
        Files.copy(source, destinationTwo, StandardCopyOption.REPLACE_EXISTING);
    }

    public OrderServiceTest() {
    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of addOrder method, of class OrderService.
     */
    @Test
    public void testAddOrder() {
        Order newOrder = new Order();
        LocalDate ld = LocalDate.now();
        ld = LocalDate.parse("04012019", DateTimeFormatter.ofPattern("MMddyyyy"));
        
        newOrder.setDate(ld);
        newOrder.setCustomerName("Service Company");
        newOrder.setState("MN");
        newOrder.setProductType("Slate");
        newOrder.setArea(new BigDecimal("100.00"));
        service.addOrder(newOrder);
    }

}
