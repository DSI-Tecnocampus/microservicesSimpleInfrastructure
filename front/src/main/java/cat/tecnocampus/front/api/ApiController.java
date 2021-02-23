package cat.tecnocampus.front.api;

import cat.tecnocampus.front.application.ApplicationController;
import cat.tecnocampus.front.domain.Product;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/products")
    public void createProduct(@RequestBody Product product) {
        System.out.println("name: " + product.getName() + " description: " + product.getDescription());
        applicationController.createProduct(product);
    }

}
