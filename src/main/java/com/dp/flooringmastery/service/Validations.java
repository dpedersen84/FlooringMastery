package com.dp.flooringmastery.service;

import com.dp.flooringmastery.data.ProductDao;
import com.dp.flooringmastery.data.ProductFileDao;
import com.dp.flooringmastery.data.TaxRateDao;
import com.dp.flooringmastery.data.TaxRateFileDao;
import com.dp.flooringmastery.models.Product;
import com.dp.flooringmastery.models.TaxRate;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public final class Validations {

    public static boolean isNullOrWhitespace(String value) {
        return value == null || value.trim().length() == 0;
    }
    
    public static boolean isValidState(String state) {
        TaxRateDao taxRateDao = new TaxRateFileDao();
        List<TaxRate> taxRates = taxRateDao.getAllTaxRates();
        
        for (TaxRate r : taxRates) {
            if (state.equalsIgnoreCase(r.getState())) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isValidProduct(String product) {
        ProductDao productDao = new ProductFileDao();
        List<Product> products = productDao.getAllProducts();
        
        for (Product p : products) {
            if (product.equalsIgnoreCase(p.getName())) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isValidDate(LocalDate date) {
        return date !=null;
    }
    
    public static boolean isValidArea(BigDecimal area) {
        return area !=null;
    }
    
}
