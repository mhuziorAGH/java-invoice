package pl.edu.agh.mwo.invoice;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import pl.edu.agh.mwo.invoice.product.Product;

public class Invoice {

    private static int nextNumber = 1;

    private final int number;

    private Map<Product, Integer> products = new LinkedHashMap<Product, Integer>();

    public Invoice() {
        this.number = nextNumber++;
    }

    public int getNumber() {
        return number;
    }

    public String getPrintout() {
        StringBuilder builder = new StringBuilder();
        builder.append("Faktura numer: ").append(this.number).append("\n");

        for (Map.Entry<Product, Integer> entry : this.products.entrySet()) {
            Product product = entry.getKey();
            Integer quantity = entry.getValue();
            builder.append(product.getName())
                    .append(", ")
                    .append(quantity)
                    .append(", ")
                    .append(product.getPrice())
                    .append("\n");
        }

        //Liczone po kluczach, klucz = pozycja na fv
        builder.append("Liczba pozycji: ").append(this.products.size());
        return builder.toString();

    }


    public void addProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cant be null");
        }
        this.addProduct(product, 1);
    }

    public void addProduct(Product product, Integer quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product cant be null");
        }
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        Integer existingQuantity = this.products.get(product);
        int newQuantity = (existingQuantity == null ? 0 : existingQuantity) + quantity;
        this.products.put(product, newQuantity);
    }

    public BigDecimal getNetValue() {
        BigDecimal value = BigDecimal.ZERO;

        for (Product product : this.products.keySet()) {
            Integer quantity = this.products.get(product);
            BigDecimal price = product.getPrice();
            price = price.multiply(BigDecimal.valueOf(quantity));
            value = value.add(price);
        }

        return value;
    }

    public BigDecimal getTax() {
        return getGrossValue().subtract(getNetValue());
    }

    public BigDecimal getGrossValue() {
        BigDecimal value = BigDecimal.ZERO;

        for (Product product : this.products.keySet()) {
            Integer quantity = this.products.get(product);
            BigDecimal price = product.getPriceWithTax();
            price = price.multiply(BigDecimal.valueOf(quantity));
            value = value.add(price);
        }

        return value;

    }
}
