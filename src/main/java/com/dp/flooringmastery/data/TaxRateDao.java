package com.dp.flooringmastery.data;

import com.dp.flooringmastery.models.TaxRate;
import java.util.List;

public interface TaxRateDao {
    
    TaxRate getTaxRate();
    
    List<TaxRate> getAllTaxRates();
}
