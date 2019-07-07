package com.dp.flooringmastery.data;

import com.dp.flooringmastery.models.TaxRate;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class TaxRateFileDao implements TaxRateDao {

    private final Map<String, TaxRate> allTaxRates = new HashMap<>();

    String path = "Taxes.txt";

    @Override
    public TaxRate getTaxRate(String state) {
        if (allTaxRates.isEmpty()) {
            try {
                loadDatabase();
            } catch (FileStorageException ex) {
                return null;
            }
        }
        return allTaxRates.get(state);
    }

    @Override
    public List<TaxRate> getAllTaxRates() {
        if (allTaxRates.isEmpty()) {
            try {
                loadDatabase();
            } catch (FileStorageException ex) {
                return new ArrayList<>();
            }
        }
        return allTaxRates.values().stream().collect(Collectors.toList());
    }

    private TaxRate mapToTaxRate(String taxRateString) {
        String[] tokens = taxRateString.split(",");

        TaxRate taxRate = new TaxRate();
        taxRate.setState(tokens[0]);
        taxRate.setRate(new BigDecimal(tokens[1]));

        return taxRate;
    }

    private void loadDatabase() throws FileStorageException {
        Scanner scanner;

        try {
            scanner = new Scanner(new BufferedReader(new FileReader(path)));
        } catch (FileNotFoundException e) {
            throw new FileStorageException("Could not load tax rates.");
        }

        String currentLine;
        TaxRate currentTaxRate;

        scanner.nextLine(); // Remove header

        while (scanner.hasNextLine()) {
            currentLine = scanner.nextLine();
            currentTaxRate = mapToTaxRate(currentLine);

            allTaxRates.put(currentTaxRate.getState(), currentTaxRate);
        }
        scanner.close();
    }
}