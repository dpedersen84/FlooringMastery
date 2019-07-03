package com.dp.flooringmastery.data;

import com.dp.flooringmastery.models.Order;
import com.dp.flooringmastery.models.Product;
import com.dp.flooringmastery.models.TaxRate;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;

public class OrderDaoTest {

    String path = "orders-test/";
    
    private static final String SEED_FILE = "orders-test/test-seed.txt";
    private static final String DATA_FILE = "orders-test/Orders_04012019.txt";
    private static final String DATA_FILE_TWO = "orders-test/Orders_04032019.txt";

    OrderDao dao = new OrderFileDao(path);
    
    TaxRateDao taxRateDao = new TaxRateFileDao("Taxes.txt");
    ProductDao productDao = new ProductFileDao("Products.txt");

    @BeforeAll
    public static void init() throws IOException {
        Path source = Paths.get(SEED_FILE);
        Path destination = Paths.get(DATA_FILE);
        Path destinationTwo = Paths.get(DATA_FILE_TWO);
        Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
        Files.copy(source, destinationTwo, StandardCopyOption.REPLACE_EXISTING);
    }

    @Test
    public void testFindById() {
        Order foundOrder = dao.findById(3, "04012019", "orders-test");

        assertNotNull(foundOrder);
    }

    @Test
    public void testAdd() throws FileStorageException {
        Order testOrder = new Order();
        testOrder.setOrderNumber(99);
        testOrder.setCustomerName("Test Company");

        TaxRate testTaxRate = taxRateDao.getTaxRate("MN");
        String testState = testTaxRate.getState();
        testOrder.setState(testState);
        BigDecimal testRate = testTaxRate.getRate();
        testOrder.setTaxRate(testRate);

        Product testProduct = productDao.getProduct("Bamboo");
        testOrder.setProductType(testProduct.getName());
        testOrder.setArea(new BigDecimal("10.00"));
        testOrder.setCostPerSqFt(testProduct.getCostPerSqFt());
        testOrder.setLaborCostPerSqFt(testProduct.getLaborCostPerSqFt());

        BigDecimal testMaterialCost = testOrder.getArea()
                .multiply(testProduct.getCostPerSqFt());
        testMaterialCost = testMaterialCost.setScale(2, RoundingMode.HALF_UP);
        testOrder.setMaterialCost(testMaterialCost);

        BigDecimal testLaborCost = testOrder.getArea()
                .multiply(testProduct.getLaborCostPerSqFt());
        testLaborCost = testLaborCost.setScale(2, RoundingMode.HALF_UP);
        testOrder.setLaborCost(testLaborCost);

        BigDecimal testTax = (testOrder.getMaterialCost()
                .add(testOrder.getLaborCost())).multiply(testOrder.getTaxRate()
                .divide(new BigDecimal("100")));
        testTax = testTax.setScale(2, RoundingMode.HALF_UP);
        testOrder.setTax(testTax);

        BigDecimal testTotal = testOrder.getLaborCost()
                .add(testOrder.getMaterialCost().add(testOrder.getTax()));
        testTotal = testTotal.setScale(2, RoundingMode.HALF_UP);
        testOrder.setTotal(testTotal);

        dao.add(testOrder, "07022019", "orders-test");
    }

    @Test
    public void testFindByDate() {
        List<Order> testList = dao.findByDate("04012019", "orders-test");

        assertEquals(4, testList.size());
    }

    @Test
    public void testEditOrder() throws FileStorageException {
        Order editedOrder = new Order();
        editedOrder.setOrderNumber(1);
        editedOrder.setCustomerName("Test Company Edit");

        TaxRate testTaxRate = taxRateDao.getTaxRate("WI");
        String testState = testTaxRate.getState();
        editedOrder.setState(testState);
        BigDecimal testRate = testTaxRate.getRate();
        editedOrder.setTaxRate(testRate);

        Product testProduct = productDao.getProduct("Slate");
        editedOrder.setProductType(testProduct.getName());
        editedOrder.setArea(new BigDecimal("10.00"));
        editedOrder.setCostPerSqFt(testProduct.getCostPerSqFt());
        editedOrder.setLaborCostPerSqFt(testProduct.getLaborCostPerSqFt());

        BigDecimal testMaterialCost = editedOrder.getArea()
                .multiply(testProduct.getCostPerSqFt());
        testMaterialCost = testMaterialCost.setScale(2, RoundingMode.HALF_UP);
        editedOrder.setMaterialCost(testMaterialCost);

        BigDecimal testLaborCost = editedOrder.getArea()
                .multiply(testProduct.getLaborCostPerSqFt());
        testLaborCost = testLaborCost.setScale(2, RoundingMode.HALF_UP);
        editedOrder.setLaborCost(testLaborCost);

        BigDecimal testTax = (editedOrder.getMaterialCost()
                .add(editedOrder.getLaborCost())).multiply(editedOrder.getTaxRate()
                .divide(new BigDecimal("100")));
        testTax = testTax.setScale(2, RoundingMode.HALF_DOWN);
        editedOrder.setTax(testTax);

        BigDecimal testTotal = editedOrder.getLaborCost()
                .add(editedOrder.getMaterialCost().add(editedOrder.getTax()));
        testTotal = testTotal.setScale(2, RoundingMode.HALF_DOWN);
        editedOrder.setTotal(testTotal);
        
        assertTrue(dao.edit(editedOrder, "04012019", "orders-test"));
    }

    @Test
    public void testRemoveOrder() throws FileStorageException {
        assertTrue(dao.delete(1, "04032019", "orders-test"));

        int endSize = dao.findByDate("04012019", "orders-test").size();
        
        assertEquals(4, endSize);
    }
}
