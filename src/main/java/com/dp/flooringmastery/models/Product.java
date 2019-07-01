package com.dp.flooringmastery.models;

import java.math.BigDecimal;

public class Product {
    private String name;
    private BigDecimal costPerSqFt;
    private BigDecimal laborCostPerSqFt;

    public Product() {
    }
    
    public Product(String name, BigDecimal costPerSqFt, BigDecimal laborCostPerSqFt) {
        this.name = name;
        this.costPerSqFt = costPerSqFt;
        this.laborCostPerSqFt = laborCostPerSqFt;
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getCostPerSqFt() {
        return costPerSqFt;
    }

    public void setCostPerSqFt(BigDecimal costPerSqFt) {
        this.costPerSqFt = costPerSqFt;
    }

    public BigDecimal getLaborCostPerSqFt() {
        return laborCostPerSqFt;
    }

    public void setLaborCostPerSqFt(BigDecimal laborCostPerSqFt) {
        this.laborCostPerSqFt = laborCostPerSqFt;
    }
    
    public BigDecimal calculateTotalCostPerSqFoot(BigDecimal area) {
        return this.costPerSqFt.multiply(area);
    }

    public BigDecimal calculateTotalLaborCostPerSqFoot(BigDecimal area) {
        return this.laborCostPerSqFt.multiply(area);
    }

}
