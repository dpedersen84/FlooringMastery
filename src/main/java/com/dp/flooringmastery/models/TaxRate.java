package com.dp.flooringmastery.models;

import java.math.BigDecimal;

public class TaxRate {
    private final String state;
    private final BigDecimal rate;
    
    public TaxRate(String state, BigDecimal rate) {
        this.state = state;
        this.rate = rate;
    }
    
    public BigDecimal calculateTotalTax(BigDecimal cost) {
        return this.rate.multiply(cost);
    }
}
