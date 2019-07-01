package com.dp.flooringmastery.models;

import java.math.BigDecimal;

public class Product {
    private final String name;
    private final BigDecimal costPerSqFt;
    private final BigDecimal laborCostPerSqFt;

    private Product(String name, BigDecimal costPerSqFt, BigDecimal laborCostPerSqFt) {
        this.name = name;
        this.costPerSqFt = costPerSqFt;
        this.laborCostPerSqFt = laborCostPerSqFt;
    }

    public BigDecimal calculateTotalCostPerSqFoot(BigDecimal area) {
        return this.costPerSqFt.multiply(area);
    }

    public BigDecimal calculateTotalLaborCostPerSqFoot(BigDecimal area) {
        return this.laborCostPerSqFt.multiply(area);
    }

}
