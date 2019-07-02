package com.dp.flooringmastery.data;

import com.dp.flooringmastery.models.Order;
import com.dp.flooringmastery.models.Product;
import com.dp.flooringmastery.models.TaxRate;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class OrderDaoTest {

    String path = "/orders-test/";

    OrderDao dao = new OrderFileDao(path);
    TaxRateDao taxRateDao = new TaxRateFileDao("Taxes.txt");
    ProductDao productDao = new ProductFileDao("Products.txt");

    public OrderDaoTest() {
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
        testTax = testTax.setScale(2, RoundingMode.HALF_DOWN);
        testOrder.setTax(testTax);

        BigDecimal testTotal = testOrder.getLaborCost()
                .add(testOrder.getMaterialCost().add(testOrder.getTax()));
        testTotal = testTotal.setScale(2, RoundingMode.HALF_DOWN);
        testOrder.setTotal(testTotal);

        dao.add(testOrder, "04012019", "orders-test");

    }

    @Test
    public void testFindByDate() {
        List<Order> testList = dao.findByDate("04012019", "orders-test");
        
        assertEquals(11, testList.size());  
    }

//    @Test
//    public void testEditOrder() {
//    }
//    @Test
//    public void testRemoveOrder() throws FileStorageException {
//        List<Order> testList = dao.findByDate("04012019", "orders-test");
//        
//        int begSize = testList.size();
//        
//        assertTrue(dao.delete(10, "04012019", "orders-test"));
//        
//        int endSize = testList.size();
//        
//        assertEquals(begSize, endSize + 1);
//    }
}
