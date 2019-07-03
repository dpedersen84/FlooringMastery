package com.dp.flooringmastery.service;

import com.dp.flooringmastery.data.FileStorageException;
import com.dp.flooringmastery.data.OrderDao;
import com.dp.flooringmastery.data.ProductDao;
import com.dp.flooringmastery.data.ProductFileDao;
import com.dp.flooringmastery.data.TaxRateDao;
import com.dp.flooringmastery.data.TaxRateFileDao;
import com.dp.flooringmastery.models.Order;
import com.dp.flooringmastery.models.Product;
import com.dp.flooringmastery.models.TaxRate;
import java.math.BigDecimal;
import java.util.List;

public class OrderService {

    OrderDao orderDao;
    ProductDao productDao = new ProductFileDao("Products.txt");
    TaxRateDao taxRateDao = new TaxRateFileDao("Taxes.txt");

    public OrderService(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    public Result<Order> addOrder(Order order) {
        Result<Order> result = new Result<>();

        if (!Validations.isValidDate(order.getDate())) {
            result.addError("Please enter a date.");
        }

        if (Validations.isNullOrWhitespace(order.getCustomerName())) {
            result.addError("Please enter a customer name.");
        }

        if (!Validations.isValidState(order.getState())) {
            result.addError("Please enter a valid state.");
        }

        if (!Validations.isValidProduct(order.getProductType())) {
            result.addError("Please enter a valid product.");
        }

        if (!Validations.isValidArea(order.getArea())) {
            result.addError("Please enter an area.");
        }

        if (result.hasError()) {
            return result;
        }

        String dateAsString = order.getDate().toString();

        // Set orderNumber
        List<Order> allOrders = orderDao.findByDate(dateAsString, "orders");
        if (allOrders.isEmpty()) {
            order.setOrderNumber(1);
        } else {
            // Find last order
            Order last = allOrders.get(allOrders.size() - 1);

            order.setOrderNumber(last.getOrderNumber() + 1);
        }

        // Get product information
        Product product = productDao.getProduct(order.getProductType());
        
        // Get tax rate for state
        TaxRate stateTaxRate = taxRateDao.getTaxRate(order.getState());
        
        // Set order fields
        order.setCostPerSqFt(product.getCostPerSqFt());
        order.setLaborCostPerSqFt(product.getLaborCostPerSqFt());
        order.setTaxRate(stateTaxRate.getRate());
        
        BigDecimal cpsf = order.getCostPerSqFt();
        BigDecimal lcpsf = order.getLaborCostPerSqFt();
        BigDecimal area = order.getArea();
        BigDecimal taxRate = order.getTaxRate();

        // Calculate material cost
        BigDecimal materialCost = Calculations.calcMaterialCost(cpsf, area);
        order.setMaterialCost(materialCost);

        // Calculate labor cost
        BigDecimal laborCost = Calculations.calcLaborCost(lcpsf, area);
        order.setLaborCost(laborCost);

        // Calculate tax
        BigDecimal tax = Calculations.calcTax(materialCost, laborCost, taxRate);
        order.setTax(tax);

        // Calculate total
        BigDecimal total = Calculations.calcTotal(materialCost, laborCost, tax);
        order.setTotal(total);

        try {
            orderDao.add(order, dateAsString, "orders");
            result.setValue(order);
        } catch (FileStorageException ex) {
            result.addError(ex.getMessage());
        }

        return result;
    }
}
