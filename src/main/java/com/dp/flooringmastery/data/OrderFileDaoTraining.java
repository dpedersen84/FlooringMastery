package com.dp.flooringmastery.data;

import com.dp.flooringmastery.models.Order;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class OrderFileDaoTraining implements OrderDao {

    private final Map<Integer, Order> allOrders = new HashMap<>();

    public String path = "orders/";
    public String homeFolder = "orders/";

    public OrderFileDaoTraining() {
    }

    public OrderFileDaoTraining(String path) {
        this.path = path;
    }

    @Override
    public void add(Order order, String date, String folder)
            throws FileStorageException {

        List<Order> orders = findByDate(date, folder);

        orders.add(order);
    }

    @Override
    public List<Order> findByDate(String date, String folder) {
        try {
            loadDatabase(folder, date);
//            loadDatabase(path, date);
        } catch (FileStorageException ex) {
            return new ArrayList<>();
        }
        return allOrders.values().stream().collect(Collectors.toList());
    }

    @Override
    public boolean edit(Order order, Order editedOrder, String date, String folder)
            throws FileStorageException {

        List<Order> orders = this.findByDate(date, folder);

        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getOrderNumber() == order.getOrderNumber()) {
                orders.set(i, editedOrder);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean delete(int orderNumber, String date, String folder)
            throws FileStorageException {
        List<Order> orders = this.findByDate(date, folder);
//        List<Order> orders = this.findByDate(date, path);

        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getOrderNumber() == orderNumber) {
                orders.remove(i);
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

    private void loadDatabase(String folder, String date)
            throws FileStorageException {
        Scanner scanner;

        path = String.format("%s/Orders_%s.txt", folder, date);
//        path = String.format("%s/Orders_%s.txt", path , date);

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

            allOrders.put(currentOrder.getOrderNumber(), currentOrder);
        }
        scanner.close();
    }
}
