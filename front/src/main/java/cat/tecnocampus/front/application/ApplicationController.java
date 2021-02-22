package cat.tecnocampus.front.application;

import cat.tecnocampus.front.domain.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ApplicationController {

    private RestTemplate restTemplate;

    private String productServiceUrl;

    public ApplicationController(RestTemplate restTemplate,
        @Value("${app.product-service.host}") String productServiceHost)
    {
        this.restTemplate = restTemplate;
        productServiceUrl = "http://" + productServiceHost + "/products";
    }

    public void createProduct(Product product) {
        restTemplate.postForObject(productServiceUrl, product, String.class);
    }

    public List<Product> getProducts() {
        var result = restTemplate.exchange(productServiceUrl, HttpMethod.GET,
                null, new ParameterizedTypeReference<List<Product>>() {});
        return result.getBody();
    }
}
