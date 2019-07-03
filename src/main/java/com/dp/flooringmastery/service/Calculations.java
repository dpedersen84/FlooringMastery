package com.dp.flooringmastery.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Calculations {

    public static BigDecimal calcMaterialCost(BigDecimal cpsf, BigDecimal area) {
        BigDecimal calculatedMaterialCost = cpsf.multiply(area);

        calculatedMaterialCost = calculatedMaterialCost
                .setScale(2, RoundingMode.HALF_UP);

        return calculatedMaterialCost;
    }

    public static BigDecimal calcLaborCost(BigDecimal lcpsf, BigDecimal area) {
        BigDecimal calculatedLaborCost = lcpsf.multiply(area);

        calculatedLaborCost = calculatedLaborCost
                .setScale(2, RoundingMode.HALF_UP);

        return calculatedLaborCost;
    }

    public static BigDecimal calcTax(BigDecimal mc, BigDecimal lc, BigDecimal tr) {
        BigDecimal calculatedTax = mc.add(lc).multiply(tr)
                .divide(new BigDecimal("100"));

        calculatedTax = calculatedTax.setScale(2, RoundingMode.HALF_UP);

        return calculatedTax;
    }

    public static BigDecimal calcTotal(BigDecimal mc, BigDecimal lc, BigDecimal taxes) {
        BigDecimal calculatedTotal = mc.add(lc).add(taxes);
        calculatedTotal = calculatedTotal.setScale(2, RoundingMode.HALF_UP);

        return calculatedTotal;
    }
}
