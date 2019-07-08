package com.dp.flooringmastery.data;

import com.dp.flooringmastery.models.Order;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class OrderFileDaoProduction implements OrderDao {

    private List<Order> listOfOrders = new ArrayList<>();
    public String path;
    public String folder;
    private final String HEADER = "OrderNumber,CustomerName,State,"
            + "TaxRate,ProductType,Area,CostPerSquareFoot,"
            + "LaborCostPerSquareFoot,MaterialCost,LaborCost,Tax,Total";

    public OrderFileDaoProduction(String folder) {
        this.folder = folder;
    }

    @Override
    public List<Order> findByDate(String date) {
        // clear orders from previous date if needed
        listOfOrders.clear();
        
        if (listOfOrders.isEmpty()) {
            try {
                loadDatabase(date);
            } catch (FileStorageException ex) {
                return new ArrayList<>();
            }
        }
        return listOfOrders.stream().collect(Collectors.toList());
    }

    @Override
    public void add(Order order, String date)
            throws FileStorageException {

        listOfOrders = findByDate(date);

        // Set orderNumber
        if (listOfOrders.isEmpty()) {
            order.setOrderNumber(1);
        } else {
            // Find last order
            Order last = listOfOrders.get(listOfOrders.size() - 1);

            order.setOrderNumber(last.getOrderNumber() + 1);
        }

        listOfOrders.add(order);

        writeDatabase(listOfOrders, date);
    }

    @Override
    public boolean edit(Order order, Order editedOrder, String date)
            throws FileStorageException {

        listOfOrders = findByDate(date);

        for (int i = 0; i < listOfOrders.size(); i++) {
            if (listOfOrders.get(i).getOrderNumber() == order.getOrderNumber()) {
                listOfOrders.set(i, editedOrder);
                writeDatabase(listOfOrders, date);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean delete(int orderNumber, String date)
            throws FileStorageException {

        listOfOrders = findByDate(date);

        for (int i = 0; i < listOfOrders.size(); i++) {
            if (listOfOrders.get(i).getOrderNumber() == orderNumber) {
                listOfOrders.remove(i);
                writeDatabase(listOfOrders, date);
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

    private void loadDatabase(String date)
            throws FileStorageException {
        Scanner scanner;

        path = String.format("%s/Orders_%s.txt", folder, date);

        try {
            scanner = new Scanner(new BufferedReader(new FileReader(path)));
        } catch (FileNotFoundException e) {
            throw new FileStorageException("Could not load orders.");
        }

        String currentLine;
        Order currentOrder;

        scanner.nextLine(); // Remove header

        while (scanner.hasNextLine()) {
            currentLine = scanner.nextLine();
            currentOrder = mapToOrder(currentLine);

            listOfOrders.add(currentOrder);
        }
        scanner.close();
    }

    private void writeDatabase(List<Order> orders, String date)
            throws FileStorageException {
        PrintWriter out;

        path = String.format("%s/Orders_%s.txt", folder, date);

        try {
            out = new PrintWriter(new FileWriter(path));
        } catch (IOException e) {
            throw new FileStorageException("Could not save order.", e);
        }

        String orderAsText;

        out.println(HEADER);

        for (Order currentOrder : orders) {
            orderAsText = mapToString(currentOrder);
            out.println(orderAsText);
            out.flush();
        }
        out.close();
    }
}
