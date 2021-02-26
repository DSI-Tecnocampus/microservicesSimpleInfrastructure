package cat.tecnocampus.front.api;

import cat.tecnocampus.front.application.ApplicationController;
import cat.tecnocampus.front.domain.Product;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ApiController {
    private ApplicationController applicationController;

    public ApiController(ApplicationController applicationController) {
        this.applicationController = applicationController;
    }

    @GetMapping("/products")
    public List<Product> getProducts() {
        System.out.println("The front application");
        return applicationController.getProducts();
    }

    @GetMapping("/products/{id}")
    public Product getProduct(@PathVariable long id,
                              @RequestParam(value = "delay", required = false, defaultValue = "0") int delay,
                              @RequestParam(value = "faultRatio", required = false, defaultValue = "0") int faultRatio) {
        System.out.println("The front application");
        return applicationController.getProduct(id, delay, faultRatio);
    }

    @PostMapping("/products")
    public void createProduct(@RequestBody Product product) {
        System.out.println("name: " + product.getName() + " description: " + product.getDescription());
        applicationController.createProduct(product);
    }

}
