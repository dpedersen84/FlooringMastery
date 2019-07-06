package com.dp.flooringmastery.data;

import com.dp.flooringmastery.models.Product;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ProductFileDao implements ProductDao {

    private final Map<String, Product> allProducts = new HashMap<>();

    String path = "products.txt";

//    public ProductFileDao(String path) {
//        this.path = path;
//    }

    @Override
    public Product getProduct(String name) {
        if (allProducts.isEmpty()) {
            try {
                loadDatabase();
            } catch (FileStorageException ex) {
                return null;
            }
        }
        return allProducts.get(name);
    }

    @Override
    public List<Product> getAllProducts() {
        if (allProducts.isEmpty()) {
            try {
                loadDatabase();
            } catch (FileStorageException ex) {
                return new ArrayList<>();
            }
        }
        return allProducts.values().stream().collect(Collectors.toList());
    }

    private Product mapToProduct(String productAsString) {
        String[] tokens = productAsString.split(",");

        Product product = new Product();
        product.setName(tokens[0]);
        product.setCostPerSqFt(new BigDecimal(tokens[1]));
        product.setLaborCostPerSqFt(new BigDecimal(tokens[2]));

        return product;
    }

    private String mapToString(Product product) {
        return String.format("%s,%s,%s",
                product.getName(),
                product.getCostPerSqFt(),
                product.getLaborCostPerSqFt());
    }

    private void loadDatabase() throws FileStorageException {
        Scanner scanner;

        try {
            scanner = new Scanner(new BufferedReader(new FileReader(path)));
        } catch (FileNotFoundException e) {
            throw new FileStorageException("Could not load products.");
        }

        String currentLine;
        Product currentProduct;

        scanner.nextLine(); // Remove header
        
        while (scanner.hasNextLine()) {
            currentLine = scanner.nextLine();
            currentProduct = mapToProduct(currentLine);

            allProducts.put(currentProduct.getName(), currentProduct);
        }
        scanner.close();
    }

    private void writeDatabase() throws FileStorageException {
        PrintWriter out;

        try {
            out = new PrintWriter(new FileWriter(path));
        } catch (IOException e) {
            throw new FileStorageException("Could not save item.", e);
        }

        String productAsText;

        List<Product> productList = this.getAllProducts();

        for (Product currentProduct : productList) {
            productAsText = mapToString(currentProduct);
            out.println(productAsText);
            out.flush();
        }
        out.close();
    }
}
