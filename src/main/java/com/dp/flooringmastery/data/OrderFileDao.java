package com.dp.flooringmastery.data;

import com.dp.flooringmastery.models.Order;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class OrderFileDao implements OrderDao {
    
    private final Map<Integer, Order> allOrders = new HashMap<>();
    
    public String path = "orders/";
    
    public OrderFileDao() {
    }
    
    public OrderFileDao(String path) {
        this.path = path;
    }
    
    @Override
    public void add(Order order, String date, String folder) 
            throws FileStorageException {
        
        List<Order> orders = findByDate(date, folder);
        
        orders.add(order);
        
        writeDatabase(orders, folder, date);
    }

    @Override
    public List<Order> findByDate(String date, String folder) {
        try {
            loadDatabase(folder, date);
        } catch (FileStorageException ex) {
            return new ArrayList<>();
        }
        return allOrders.values().stream().collect(Collectors.toList());
    }

    @Override
    public boolean edit(Order order, String date, String folder) 
            throws FileStorageException {
       
        List<Order> orders = this.findByDate(date, folder);
        
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getOrderNumber() == order.getOrderNumber()) {
                orders.set(i, order);
                writeDatabase(orders, folder, date);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean delete(int orderNumber, String date, String folder) 
            throws FileStorageException {
        List<Order> orders = this.findByDate(date, folder);
        
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getOrderNumber() == orderNumber) {
                orders.remove(i);
                writeDatabase(orders, folder, date);
                return true;
            }
        }
        return false;
    }
    
    private Order mapToOrder(String orderAsString) {
        String[] tokens = orderAsString.split(",");

        Order order = new Order();
        order.setOrderNumber(Integer.parseInt(tokens[0]));
        order.setCustomerName(tokens[1]);
        order.setState(tokens[2]);
        order.setTaxRate(new BigDecimal(tokens[3]));
        order.setProductType(tokens[4]);
        order.setArea(new BigDecimal(tokens[5]));
        order.setCostPerSqFt(new BigDecimal(tokens[6]));
        order.setLaborCostPerSqFt(new BigDecimal(tokens[7]));
        order.setMaterialCost(new BigDecimal(tokens[8]));
        order.setLaborCost(new BigDecimal(tokens[9]));
        order.setTax(new BigDecimal(tokens[10]));
        order.setTotal(new BigDecimal(tokens[11]));

        return order;
    }

    private String mapToString(Order order) {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
                order.getOrderNumber(),
                order.getCustomerName(),
                order.getState(),
                order.getTaxRate(),
                order.getProductType(),
                order.getArea(),
                order.getCostPerSqFt(),
                order.getLaborCostPerSqFt(),
                order.getMaterialCost(),
                order.getLaborCost(),
                order.getTax(),
                order.getTotal());
    }

    private void loadDatabase(String folder, String date) 
            throws FileStorageException {
        Scanner scanner;
        
        path = String.format("%s/Orders_%s.txt", folder, date);
        
        try {
            scanner = new Scanner(new BufferedReader(new FileReader(path)));
        } catch (FileNotFoundException e) {
            throw new FileStorageException("Could not load products.");
        }
       
        String currentLine;
        Order currentOrder;
        
        scanner.nextLine(); // Remove header

        while (scanner.hasNextLine()) {
            currentLine = scanner.nextLine();
            currentOrder = mapToOrder(currentLine);

            allOrders.put(currentOrder.getOrderNumber(), currentOrder);
        }
        scanner.close();
    }

    private void writeDatabase(List<Order> orders, String folder, String date) 
            throws FileStorageException {
        PrintWriter out;
        
        path = String.format("%s/Orders_%s.txt", folder, date);

        try {
            out = new PrintWriter(new FileWriter(path));
        } catch (IOException e) {
            throw new FileStorageException("Could not save item.", e);
        }
        
        String orderAsText;

        out.println("OrderNumber,CustomerName,State,TaxRate,ProductType,"
                + "Area,CostPerSquareFoot,LaborCostPerSquareFoot,"
                + "MaterialCost,LaborCost,Tax,Total");
        
        for (Order currentOrder : orders) {
            orderAsText = mapToString(currentOrder);
            out.println(orderAsText);
            out.flush();
        }
        out.close();
    }

    private String turnDateToString(LocalDate date) {
        LocalDate ld = LocalDate.now();
        String stringDate = ld.toString();
        ld = LocalDate.parse(stringDate, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        String formatted = ld.format(DateTimeFormatter.ofPattern("MMddyyyy"));
        return formatted;
    }
}
