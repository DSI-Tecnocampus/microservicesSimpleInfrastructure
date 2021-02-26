package cat.tecnocampus.product.api;

import cat.tecnocampus.product.application.ProductPersistence;
import cat.tecnocampus.product.domain.Product;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

@RestController
public class ApiController {

    private ProductPersistence persistence;

    public ApiController(ProductPersistence persistence) {
        this.persistence = persistence;
    }

    @GetMapping("/products")
    public List<Product> getProducts() {
        System.out.println("Getting products!!!!!");
        return persistence.getProducts();
    }

    @GetMapping("/products/{id}")
    public Product getProducts(@PathVariable long id,
                               @RequestParam(value = "delay", required = false, defaultValue = "0") int delay,
                               @RequestParam(value = "faultRatio", required = false, defaultValue = "0") int faultRatio) {
        System.out.println("Getting ONE product!!!!!");
        if (delay > 0) simulateDelay(delay);
        if (faultRatio > 0) throwErrorIfBadLuck(faultRatio);
        return persistence.getProduct(id);
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

    private void simulateDelay(int delay) {
        System.out.println("Sleeping for " + delay + " seconds...");
        try {Thread.sleep(delay * 1000);} catch (InterruptedException e) {}
        System.out.println("Moving on...");
    }

    private void throwErrorIfBadLuck(int faultRatio) {
        int randomThreshold = getRandomNumber(1, 100);
        if (faultRatio < randomThreshold) {
            System.out.println("We got lucky, no error occurred ...");
        } else {
            System.out.println("Bad luck, an error occurred... " + faultRatio);
            throw new RuntimeException("Something went wrong...");
        }
    }

    private final Random randomNumberGenerator = new Random();
    private int getRandomNumber(int min, int max) {
        if (max < min) {
            throw new RuntimeException("Max must be greater than min");
        }
        return randomNumberGenerator.nextInt((max - min) + 1) + min;
    }
}
