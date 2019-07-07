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

    public Result<Order> addOrder(Order order, String folder)
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
        List<Order> allOrders = orderDao.findByDate(dateAsString, folder);
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
            orderDao.add(order, dateAsString, folder);
            result.setValue(order);
        } catch (FileStorageException ex) {
            result.addError(ex.getMessage());
        }

        return result;
    }

    public List<Order> findByDate(LocalDate date, String folder)
            throws FileStorageException {

        String dateAsString = date.format(DateTimeFormatter
                .ofPattern("MMddyyyy"));

        return orderDao.findByDate(dateAsString, folder);
    }

    public Order findByOrderNumber(LocalDate date, int orderNumber, String folder)
            throws FileStorageException, InvalidOrderNumberException {

        String dateAsString = date.format(DateTimeFormatter
                .ofPattern("MMddyyyy"));

        List<Order> ordersByDate = orderDao.findByDate(dateAsString, folder);

        Order chosenOrder = ordersByDate.stream().filter(o
                -> o.getOrderNumber() == orderNumber).findFirst().orElse(null);

        if (chosenOrder != null) {
            return chosenOrder;
        } else {
            throw new InvalidOrderNumberException("No orders with that number exist.");
        }
    }

    public boolean editOrder(Order originalOrder, Order editedOrder, LocalDate date, String folder)
            throws FileStorageException {
        
        String dateAsString = date.format(DateTimeFormatter.ofPattern("MMddyyyy"));

        if (editedOrder.getCustomerName() == null || editedOrder.getCustomerName().equals("")) {
            editedOrder.setCustomerName(originalOrder.getCustomerName());
        }

        if (editedOrder.getState() == null || editedOrder.getState().equals("")) {
            editedOrder.setState(originalOrder.getState());
        }

        if (editedOrder.getProductType() == null || editedOrder.getProductType().equals("")) {
            editedOrder.setProductType(originalOrder.getProductType());
        }

        if (editedOrder.getArea() == null || editedOrder.getArea().compareTo(BigDecimal.ZERO) == 0) {
            editedOrder.setArea(originalOrder.getArea());
        }

        editedOrder.setOrderNumber(originalOrder.getOrderNumber());

        // Get product information
        Product product = productDao.getProduct(editedOrder.getProductType());

        // Get tax rate for state
        TaxRate stateTaxRate = taxRateDao.getTaxRate(editedOrder.getState());

        // Set order fields
        editedOrder.setCostPerSqFt(product.getCostPerSqFt());
        editedOrder.setLaborCostPerSqFt(product.getLaborCostPerSqFt());
        editedOrder.setTaxRate(stateTaxRate.getRate());

        BigDecimal cpsf = editedOrder.getCostPerSqFt();
        BigDecimal lcpsf = editedOrder.getLaborCostPerSqFt();
        BigDecimal area = editedOrder.getArea();
        BigDecimal taxRate = editedOrder.getTaxRate();

        // Calculate material cost
        BigDecimal materialCost = Calculations.calcMaterialCost(cpsf, area);
        editedOrder.setMaterialCost(materialCost);

        // Calculate labor cost
        BigDecimal laborCost = Calculations.calcLaborCost(lcpsf, area);
        editedOrder.setLaborCost(laborCost);

        // Calculate tax
        BigDecimal tax = Calculations.calcTax(materialCost, laborCost, taxRate);
        editedOrder.setTax(tax);

        // Calculate total
        BigDecimal total = Calculations.calcTotal(materialCost, laborCost, tax);
        editedOrder.setTotal(total);

        return orderDao.edit(originalOrder, editedOrder, dateAsString, folder);
    }

    public boolean deleteOrder(int orderNumber, LocalDate date, String folder)
            throws FileStorageException {
        
        String dateAsString = date.format(DateTimeFormatter.ofPattern("MMddyyyy"));

        return orderDao.delete(orderNumber, dateAsString, folder);
    }

    private String turnDateToString(LocalDate date) {
        
        String formatted = date.format(DateTimeFormatter.ofPattern("MMddyyyy"));
        
        return formatted;
    }
}
