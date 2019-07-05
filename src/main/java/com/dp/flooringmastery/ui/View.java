package com.dp.flooringmastery.ui;

import com.dp.flooringmastery.models.Order;
import com.dp.flooringmastery.service.Response;
import java.time.LocalDate;
import java.util.List;

public class View {

    private final UserIOConsoleImpl io;

    public View(UserIOConsoleImpl io) {
        this.io = new UserIOConsoleImpl();
    }

    public MainMenu menuSelect() {
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (MainMenu mmo : MainMenu.values()) {
            io.print(String.format("%s. %s", mmo.getValue(), mmo.getOption()));
            min = Math.min(mmo.getValue(), min);
            max = Math.max(mmo.getValue(), max);
        }
        int value = io.readInt(String.format("Select [%s-%s]:", min, max), min, max);
        return MainMenu.fromValue(value);
    }

    public boolean confirm(String message) {
        return io.readBoolean(String.format("%s [y/n]:", message));
    }

    public void hello() {
        io.print("==================");
        io.print("Welcome to Dan's Flooring!");
        io.print("==================\n");
    }

    public void goodbye() {
        io.print("==================");
        io.print("Goodbye!");
        io.print("==================\n");
    }

    Order createOrder(LocalDate date) {
        Order order = new Order();
        order.setDate(io.readLocalDate("Order Date: ", LocalDate.of(2018, 1, 1), LocalDate.of(2025, 1, 1)));
        order.setCustomerName(io.readRequiredString("Customer Name: "));
        order.setState(io.readRequiredString("State (ex: MN): "));
        order.setArea(io.readBigDecimal("Area: "));
        order.setProductType(io.readRequiredString("Product Type: "));

        return order;
    }

    void displayErrors(Response r) {
        io.print("==================");
        io.print("ERROR");
        for (String message : r.getErrors()) {
            io.print(message);
        }
        io.print("==================\n");
    }
    
    void displayOrder(Order order) {
        io.print(order.getCustomerName());
        io.print(order.getDate().toString());
        io.print(order.getState());
        io.print(order.getArea().toString());
        io.print(order.getProductType());
        io.print(order.getCostPerSqFt().toString());
        io.print(order.getLaborCostPerSqFt().toString());
        io.print("Totals:\n");
        io.print(order.getLaborCost().toString());
        io.print(order.getMaterialCost().toString());
        io.print(order.getTax().toString());
        io.print(order.getTotal().toString());
    }
    
    void displayOrders(List<Order> orders) {
        if (orders == null || orders.size() <= 0) {
            io.print("No orders for that date.\n");
        } else {
            for (Order o : orders) {
                displayOrder(o);
            }
        }
    }
    
    boolean accept(Order order) {
        io.print("==================");
        io.print("Review your order: ");
        io.print("==================\n");
        displayOrder(order);
        return confirm("Is this information correct?");
    }
}
