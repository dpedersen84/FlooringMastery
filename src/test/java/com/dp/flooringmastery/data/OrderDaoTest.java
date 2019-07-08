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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;

public class OrderDaoTest {
    
    private static final String SEED_FILE = "orders-test/test-seed.txt";
    private static final String DATA_FILE = "orders-test/Orders_04012019.txt";
    private static final String DATA_FILE_TWO = "orders-test/Orders_04032019.txt";

    OrderDao dao = new OrderFileDaoProduction("orders-test/");
    TaxRateDao taxRateDao = new TaxRateFileDao();
    ProductDao productDao = new ProductFileDao();

    @BeforeAll
    public static void init() throws IOException {
        Path source = Paths.get(SEED_FILE);
        Path destination = Paths.get(DATA_FILE);
        Path destinationTwo = Paths.get(DATA_FILE_TWO);
        Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
        Files.copy(source, destinationTwo, StandardCopyOption.REPLACE_EXISTING);
    }

    @Test
    public void testAdd() throws FileStorageException {
        Order testOrder = new Order();
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

        LocalDate ld = LocalDate.now();
        ld = LocalDate.parse("2015-01-01");
        ld = LocalDate.parse("07/02/2019", DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        String isoDate = ld.toString();
        ld = LocalDate.parse(isoDate);
        String formatted = ld.format(DateTimeFormatter.ofPattern("MMddyyyy"));
        
        dao.add(testOrder, formatted);
    }

    @Test
    public void testFindByDate() throws FileStorageException {
        LocalDate ld = LocalDate.now();
        ld = LocalDate.parse("2015-01-01");
        ld = LocalDate.parse("04/01/2019", DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        String isoDate = ld.toString();
        ld = LocalDate.parse(isoDate);
        String formatted = ld.format(DateTimeFormatter.ofPattern("MMddyyyy"));
        
        List<Order> testList = dao.findByDate(formatted);

        assertEquals(4, testList.size());
    }

    @Test
    public void testEditOrder() throws FileStorageException {
        Order orderOne = new Order();
        orderOne.setOrderNumber(1);
        orderOne.setCustomerName("Test Company");

        TaxRate testTaxRate = taxRateDao.getTaxRate("WI");
        String testState = testTaxRate.getState();
        orderOne.setState(testState);
        BigDecimal testRate = testTaxRate.getRate();
        orderOne.setTaxRate(testRate);

        Product testProduct = productDao.getProduct("Slate");
        orderOne.setProductType(testProduct.getName());
        orderOne.setArea(new BigDecimal("10.00"));
        orderOne.setCostPerSqFt(testProduct.getCostPerSqFt());
        orderOne.setLaborCostPerSqFt(testProduct.getLaborCostPerSqFt());

        BigDecimal testMaterialCost = orderOne.getArea()
                .multiply(testProduct.getCostPerSqFt());
        testMaterialCost = testMaterialCost.setScale(2, RoundingMode.HALF_UP);
        orderOne.setMaterialCost(testMaterialCost);

        BigDecimal testLaborCost = orderOne.getArea()
                .multiply(testProduct.getLaborCostPerSqFt());
        testLaborCost = testLaborCost.setScale(2, RoundingMode.HALF_UP);
        orderOne.setLaborCost(testLaborCost);

        BigDecimal testTax = (orderOne.getMaterialCost()
                .add(orderOne.getLaborCost())).multiply(orderOne.getTaxRate()
                .divide(new BigDecimal("100")));
        testTax = testTax.setScale(2, RoundingMode.HALF_DOWN);
        orderOne.setTax(testTax);

        BigDecimal testTotal = orderOne.getLaborCost()
                .add(orderOne.getMaterialCost().add(orderOne.getTax()));
        testTotal = testTotal.setScale(2, RoundingMode.HALF_DOWN);
        orderOne.setTotal(testTotal);
        
        // edited name
        Order orderTwo = new Order();
        orderTwo.setOrderNumber(1);
        orderTwo.setCustomerName("Name has been edited again!");
        orderTwo.setState(testState);
        orderTwo.setTaxRate(testRate);
        orderTwo.setProductType(testProduct.getName());
        orderTwo.setArea(new BigDecimal("10.00"));
        orderTwo.setCostPerSqFt(testProduct.getCostPerSqFt());
        orderTwo.setLaborCostPerSqFt(testProduct.getLaborCostPerSqFt());
        orderTwo.setMaterialCost(testMaterialCost);
        orderTwo.setLaborCost(testLaborCost);
        orderTwo.setTax(testTax);
        orderTwo.setTotal(testTotal);
        
        LocalDate ld = LocalDate.now();
        ld = LocalDate.parse("2015-01-01");
        ld = LocalDate.parse("04/01/2019", DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        String isoDate = ld.toString();
        ld = LocalDate.parse(isoDate);
        String formatted = ld.format(DateTimeFormatter.ofPattern("MMddyyyy"));
 
        assertTrue(dao.edit(orderOne, orderTwo, formatted));
    }

    @Test
    public void testRemoveOrder() throws FileStorageException {
        LocalDate ld = LocalDate.now();
        ld = LocalDate.parse("2015-01-01");
        ld = LocalDate.parse("04/03/2019", DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        String isoDate = ld.toString();
        ld = LocalDate.parse(isoDate);
        String formatted = ld.format(DateTimeFormatter.ofPattern("MMddyyyy"));
        
        assertTrue(dao.delete(1, formatted));

        List<Order> testList = dao.findByDate(formatted);
        assertEquals(4, testList.size());
    }
}
