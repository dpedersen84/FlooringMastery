package com.dp.flooringmastery.ui;

import com.dp.flooringmastery.data.FileStorageException;
import com.dp.flooringmastery.models.Order;
import com.dp.flooringmastery.service.InvalidOrderNumberException;
import com.dp.flooringmastery.service.OrderService;
import com.dp.flooringmastery.service.Response;
import com.dp.flooringmastery.service.Result;
import java.time.LocalDate;
import java.util.List;

public class Controller {

    private final OrderService orderService;
    private final View view;

    Controller(OrderService orderService, View view) {
        this.orderService = orderService;
        this.view = view;
    }

    public void run() {
        view.hello();
        boolean keepGoing = true;
        int menuSelection = 0;

        try {
            while (keepGoing) {
                menuSelection = getMenuSelection();

                switch (menuSelection) {
                    case 1:
                        displayOrders();
                        break;
                    case 2:
                        addOrder();
                        break;
                    case 3:
                        editOrder();
                        break;
                    case 4:
                        removeOrder();
                        break;
                    case 5:
                        keepGoing = false;
                        break;
                    default:
                        unknownCommand();
                }
            }
            exitMessage();
        } catch (FileStorageException | InvalidOrderNumberException e) {
            view.displayErrorMessage(e.getMessage());
        }

    }

    private int getMenuSelection() {
        return view.displayMainMenuAndGetSelection();
    }

    private void displayOrders() throws FileStorageException {
        view.displayDisplayOrdersBanner();
        LocalDate orderDate = view.getDateInput();
        List<Order> orders = orderService.findByDate(orderDate);
        view.displayOrders(orders);
    }

    private void addOrder() throws FileStorageException {
        view.displayAddOrderBanner();
        Order newOrder = view.createOrder();
        Result<Order> result = orderService.addOrder(newOrder);
        if (result.hasError()) {
            view.displayErrors(result);
        } else {
            view.displaySuccess();
        }
    }

    private void editOrder()
            throws FileStorageException, InvalidOrderNumberException {

        Order chosenOrder = null;

        view.displayEditOrderBanner();
        LocalDate orderDate = view.getDateInput();
        int orderNumber = view.getOrderNumber();

        try {
            chosenOrder = orderService
                    .findByOrderNumber(orderDate, orderNumber);

            view.displayOrder(chosenOrder);
            Order editedOrder = view.displayEditOrder(chosenOrder);

            Result<Order> result = orderService
                    .editOrder(chosenOrder, editedOrder, orderDate);

            if (result.hasError()) {
                view.displayErrors(result);
            } else {
                view.displaySuccess();
            }

        } catch (InvalidOrderNumberException e) {
            Response r = new Response();
            r.addError(e.getMessage());
            view.displayErrors(r);
        }

    }

    private void removeOrder()
            throws FileStorageException, InvalidOrderNumberException {

        view.displayRemoveOrderBanner();
        LocalDate orderDate = view.getDateInput();
        int orderNumber = view.getOrderNumber();
        
        try {
            Order chosenOrder = orderService
                    .findByOrderNumber(orderDate, orderNumber);
            view.displayOrder(chosenOrder);
            
            if (!view.confirm("Would you like to delete this order?")) {
                return;
            }
            
            orderService.deleteOrder(orderNumber, orderDate);
            view.displaySuccess();
            
        } catch (InvalidOrderNumberException e) {
            Response r = new Response();
            r.addError(e.getMessage());
            view.displayErrors(r);
        }
    }

    private void unknownCommand() {
        view.displayUnknownCommandBanner();
    }

    private void exitMessage() {
        view.goodbye();
    }
}
