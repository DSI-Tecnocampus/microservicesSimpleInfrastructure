package cat.tecnocampus.product.api;

import cat.tecnocampus.product.application.ProductPersistence;
import cat.tecnocampus.product.domain.Product;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ApiController {

    private ProductPersistence persistence;

    public ApiController(ProductPersistence persistence) {
        this.persistence = persistence;
    }

    @GetMapping("/products")
    public List<Product> getProducts() {
        return persistence.getProducts();
    }

    @PostMapping("/products")
    public void createProduct(@RequestBody Product product) {
        System.out.println("name: " + product.getName() + " description: " + product.getDescription());
        persistence.createProduct(product);
    }

    @GetMapping("/hello")
    public String sayHello() {
        return "{\"salutation\" : \"Hello world. \"}";
    }
}
