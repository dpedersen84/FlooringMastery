package com.dp.flooringmastery.data;

import com.dp.flooringmastery.models.TaxRate;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TaxRateDaoTest {

    private TaxRateDao dao = new TaxRateFileDao("Taxes.txt");

    public TaxRateDaoTest() {
    }

    /**
     * Test of getTaxRate method, of class TaxRateDao.
     */
    @Test
    public void testGetTaxRate() {
        String testState = "MN";

        TaxRate testTaxRate = dao.getTaxRate(testState);

        assertEquals(testTaxRate.getState(), testState);
    }

    /**
     * Test of getAllTaxRates method, of class TaxRateDao.
     */
    @Test
    public void testGetAllTaxRates() {
        List<TaxRate> taxRates = dao.getAllTaxRates();

        assertEquals(9, taxRates.size());
    }
}
