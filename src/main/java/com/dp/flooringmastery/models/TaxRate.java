package com.dp.flooringmastery.models;

import java.math.BigDecimal;

public class TaxRate {

    private String state;
    private BigDecimal rate;
    
    public TaxRate() {
    }

    public TaxRate(String state, BigDecimal rate) {
        this.state = state;
        this.rate = rate;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal calculateTotalTax(BigDecimal cost) {
        return this.rate.multiply(cost);
    }
}
