package com.dp.flooringmastery.service;

import com.dp.flooringmastery.data.FileStorageException;
import com.dp.flooringmastery.data.OrderDao;
import com.dp.flooringmastery.data.ProductDao;
import com.dp.flooringmastery.data.TaxRateDao;
import com.dp.flooringmastery.models.Order;
import com.dp.flooringmastery.models.Product;
import com.dp.flooringmastery.models.TaxRate;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class OrderService {

    OrderDao orderDao;
    ProductDao productDao;
    TaxRateDao taxRateDao;

    public OrderService(OrderDao orderDao, ProductDao productDao, TaxRateDao taxRateDao) {
        this.orderDao = orderDao;
        this.productDao = productDao;
        this.taxRateDao = taxRateDao;
    }

    public Result<Order> addOrder(Order order) 
            throws FileStorageException {
        Result<Order> result = new Result<>();

        if (!Validations.isValidDate(order.getDate())) {
            result.addError("Please enter a date (Ex. 07/01/2019).");
        }

        if (Validations.isNullOrWhitespace(order.getCustomerName())) {
            result.addError("Please enter a customer name.");
        }

        if (!Validations.isValidState(order.getState())) {
            result.addError("Please enter a valid state (Ex. MN).");
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

        String dateAsString = turnDateToString(order.getDate());

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

    public List<Order> findByDate(LocalDate date, String folder) 
            throws FileStorageException {
        String stringDate = turnDateToString(date);

        return orderDao.findByDate(stringDate, folder);
    }
    
    public Order findByOrderNumber(LocalDate date, int orderNumber, String folder)
            throws FileStorageException, InvalidOrderNumberException {
        
        String stringDate = turnDateToString(date);
        
        List<Order> ordersByDate = orderDao.findByDate(stringDate, folder);
        
        Order chosenOrder = ordersByDate.stream().filter(o -> 
                o.getOrderNumber() == orderNumber).findFirst().orElse(null);
        
        if(chosenOrder != null) {
            return chosenOrder;
        } else {
            throw new InvalidOrderNumberException("ERROR: No orders with that number exist.");
        }
    }

    public boolean deleteOrder(int orderNumber, LocalDate date, String folder)
            throws FileStorageException {
        String stringDate = turnDateToString(date);
        
        return orderDao.delete(orderNumber, stringDate, folder);
    }

    private String turnDateToString(LocalDate date) {
        String formatted = date.format(DateTimeFormatter.ofPattern("MMddyyyy"));
        return formatted;
    }
}
