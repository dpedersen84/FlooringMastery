package com.dp.flooringmastery.ui;

import com.dp.flooringmastery.models.Order;
import com.dp.flooringmastery.service.Response;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class View {

    private final UserIOConsoleImpl io;

    public View(UserIOConsoleImpl io) {
        this.io = new UserIOConsoleImpl();
    }

    // menu
    public int displayMainMenuAndGetSelection() {
        io.print("1. Display Orders");
        io.print("2. Add Order");
        io.print("3. Edit Order");
        io.print("4. Delete Order");
        io.print("5. Exit");
        io.print("");
        return io.readInt("What would you like to do? [1-5]", 1, 5);
    }

    public boolean confirm(String message) {
        return io.readBoolean(String.format("%s [y/n]:", message));
    }

    // banners
    public void hello() {
        io.print("* * * * * * * * * * \n");
        io.print("Dan's Flooring!\n");
        io.print("* * * * * * * * * * \n");
    }

    public void goodbye() {
        io.print("==================");
        io.print("Goodbye!");
        io.print("==================\n");
    }

    void displayDisplayOrdersBanner() {
        io.print("==================");
        io.print("Display Orders!");
        io.print("==================\n");
    }

    void displayAddOrderBanner() {
        io.print("==================");
        io.print("Add Order!");
        io.print("==================\n");
    }

    void displayEditOrderBanner() {
        io.print("==================");
        io.print("Edit Order!");
        io.print("==================\n");
    }

    void displayRemoveOrderBanner() {
        io.print("==================");
        io.print("Remove Order!");
        io.print("==================\n");
    }

    void displaySuccess() {
        io.print("==================");
        io.print("Success!");
        io.print("==================\n");
    }

    void displayUnknownCommandBanner() {
        io.print("Unknown Command!");
    }

    void displayErrorMessage(String errorMsg) {
        io.print(errorMsg);
    }

    // input 
    LocalDate getDateInput() {
        return io.readLocalDate("Order Date(Ex. 04/01/2019): ");
    }

    int getOrderNumber() {
        return io.readInt("Order Number: ");
    }

    String getCustomerNameInput() {
        return io.readString("Customer Name: ");
    }

    String getProductInput() {
        return io.readString("Product: ");
    }

    BigDecimal getAreaInput() {
        return io.readBigDecimal("Area: ");
    }
    
    String getStateInput() {
        return io.readString("State: ");
    }

    // create order
    Order createOrder() {
        Order order = new Order();
        order.setDate(io.readLocalDate("Order Date(Ex. 04/01/2019): ",
                LocalDate.of(2018, 1, 1), LocalDate.of(2025, 1, 1)));
        order.setCustomerName(io.readRequiredString("Customer Name: "));
        order.setState(io.readRequiredString("State (ex: MN): "));
        order.setArea(io.readBigDecimal("Area: "));
        order.setProductType(io.readRequiredString("Product Type: "));

        return order;
    }

    // displays
    void displayErrors(Response r) {
        io.print("==================");
        io.print("ERROR");
        for (String message : r.getErrors()) {
            io.print(message);
        }
        io.print("==================\n");
    }

    void displayOrder(Order o) {
        io.print(String.format("%s. %s", o.getOrderNumber(), o.getCustomerName()));
        io.print(String.format("State: %s", o.getState()));
        io.print(String.format("Area: %s", o.getArea().toString()));
        io.print(String.format("Product: %s", o.getProductType()));
        io.print(String.format("Cost per Square Foot: $%s",
                o.getCostPerSqFt().toString()));
        io.print(String.format("Labor Cost per Square Foot: $%s",
                o.getLaborCostPerSqFt().toString()));
        io.print(String.format("Labor Cost: $%s", o.getLaborCost().toString()));
        io.print(String.format("Material Cost: $%s", o.getMaterialCost().toString()));
        io.print(String.format("Tax: $%s", o.getTax().toString()));
        io.print(String.format("Total: $%s", o.getTotal().toString()));
        io.print("");
    }

    Order displayEditOrder(Order o) {
        Order editedOrder = new Order();
        
        io.print(String.format("Customer Name: %s", o.getCustomerName()));
        editedOrder.setCustomerName(getCustomerNameInput());
        
        io.print(String.format("State: %s", o.getState()));
        editedOrder.setState(getStateInput());
        
        io.print(String.format("Area: %s", o.getArea().toString()));
        editedOrder.setArea(getAreaInput());
        
        io.print(String.format("Product: %s", o.getProductType()));
        editedOrder.setProductType(getProductInput());
        
        return editedOrder;
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
