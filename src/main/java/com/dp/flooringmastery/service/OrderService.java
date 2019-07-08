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

        String dateAsString = order.getDate().format(DateTimeFormatter.ofPattern("MMddyyyy"));

        // Set orderNumber
        List<Order> allOrders = orderDao.findByDate(dateAsString);
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
            orderDao.add(order, dateAsString);
            result.setValue(order);
        } catch (FileStorageException ex) {
            result.addError(ex.getMessage());
        }

        return result;
    }

    public List<Order> findByDate(LocalDate date)
            throws FileStorageException {

        String dateAsString = date.format(DateTimeFormatter
                .ofPattern("MMddyyyy"));

        return orderDao.findByDate(dateAsString);
    }

    public Order findByOrderNumber(LocalDate date, int orderNumber)
            throws FileStorageException, InvalidOrderNumberException {

        String dateAsString = date.format(DateTimeFormatter
                .ofPattern("MMddyyyy"));

        List<Order> ordersByDate = orderDao.findByDate(dateAsString);

        Order chosenOrder = ordersByDate.stream().filter(o
                -> o.getOrderNumber() == orderNumber).findFirst().orElse(null);

        if (chosenOrder != null) {
            return chosenOrder;
        } else {
            throw new InvalidOrderNumberException("No orders with that number exist.");
        }
    }

    public Result<Order> editOrder(Order oldOrder, Order newOrder, LocalDate date)
            throws FileStorageException {

        Result<Order> result = new Result<>();

        String dateAsString = date.format(DateTimeFormatter.ofPattern("MMddyyyy"));

        if (newOrder.getCustomerName() == null
                || newOrder.getCustomerName().equals("")) {
            newOrder.setCustomerName(oldOrder.getCustomerName());
        }

        if (newOrder.getState() == null
                || newOrder.getState().equals("")) {
            newOrder.setState(oldOrder.getState());
        }

        if (newOrder.getProductType() == null
                || newOrder.getProductType().equals("")) {
            newOrder.setProductType(oldOrder.getProductType());
        }

        if (newOrder.getArea() == null
                || newOrder.getArea().compareTo(BigDecimal.ZERO) == 0) {
            newOrder.setArea(oldOrder.getArea());
        }

        newOrder.setOrderNumber(oldOrder.getOrderNumber());

        if (!Validations.isValidState(newOrder.getState())) {
            result.addError("Please enter a valid state (Ex. MN).");
        }

        if (!Validations.isValidProduct(newOrder.getProductType())) {
            result.addError("Please enter a valid product.");
        }

        if (result.hasError()) {
            return result;
        }

        // Get product information
        Product product = productDao.getProduct(newOrder.getProductType());

        // Get tax rate for state
        TaxRate stateTaxRate = taxRateDao.getTaxRate(newOrder.getState());

        // Set order fields
        newOrder.setCostPerSqFt(product.getCostPerSqFt());
        newOrder.setLaborCostPerSqFt(product.getLaborCostPerSqFt());
        newOrder.setTaxRate(stateTaxRate.getRate());

        BigDecimal cpsf = newOrder.getCostPerSqFt();
        BigDecimal lcpsf = newOrder.getLaborCostPerSqFt();
        BigDecimal area = newOrder.getArea();
        BigDecimal taxRate = newOrder.getTaxRate();

        // Calculate material cost
        BigDecimal materialCost = Calculations.calcMaterialCost(cpsf, area);
        newOrder.setMaterialCost(materialCost);

        // Calculate labor cost
        BigDecimal laborCost = Calculations.calcLaborCost(lcpsf, area);
        newOrder.setLaborCost(laborCost);

        // Calculate tax
        BigDecimal tax = Calculations.calcTax(materialCost, laborCost, taxRate);
        newOrder.setTax(tax);

        // Calculate total
        BigDecimal total = Calculations.calcTotal(materialCost, laborCost, tax);
        newOrder.setTotal(total);

        try {
            orderDao.edit(oldOrder, newOrder, dateAsString);
            result.setValue(newOrder);
        } catch (FileStorageException ex) {
            result.addError(ex.getMessage());
        }
        return result;
    }

    public boolean deleteOrder(int orderNumber, LocalDate date)
            throws FileStorageException {

        String dateAsString = date.format(DateTimeFormatter.ofPattern("MMddyyyy"));

        return orderDao.delete(orderNumber, dateAsString);
    }
}
