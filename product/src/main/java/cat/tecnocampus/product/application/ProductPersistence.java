package cat.tecnocampus.product.application;

import cat.tecnocampus.product.domain.Product;

import java.util.List;

public interface ProductPersistence {
    public List<Product> getProducts();

    public void createProduct(Product product);
}
