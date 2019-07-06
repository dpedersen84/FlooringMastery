package com.dp.flooringmastery;

import com.dp.flooringmastery.ui.Controller;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App {

    public static void main(String[] args) {
        ApplicationContext ctx
                = new ClassPathXmlApplicationContext("context.xml");
        Controller controller
                = ctx.getBean("controller", Controller.class);
        controller.run();
    }
}
