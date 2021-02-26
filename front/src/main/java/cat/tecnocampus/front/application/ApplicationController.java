package cat.tecnocampus.front.application;

import cat.tecnocampus.front.domain.Product;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ApplicationController {

    private RestTemplate restTemplate;
    private CircuitBreakerFactory circuitBreakerFactory;

    private String productServiceUrl;

    public ApplicationController(RestTemplate restTemplate,
                                 @Value("${app.product-service.host}") String productServiceHost,
                                 CircuitBreakerFactory circuitBreakerFactory)
    {
        this.restTemplate = restTemplate;
        productServiceUrl = "http://" + productServiceHost + "/products";
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    public void createProduct(Product product) {
        restTemplate.postForObject(productServiceUrl, product, String.class);
    }

    public List<Product> getProducts() {
        var result = restTemplate.exchange(productServiceUrl, HttpMethod.GET,
                null, new ParameterizedTypeReference<List<Product>>() {});
        return result.getBody();
    }

    @Retry(name="product")
    public Product getProduct(long id, int delay, int faultRatio) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("product");
        var url = productServiceUrl + "/" + id + "?delay=" + delay + "&faultRatio=" + faultRatio;

        return circuitBreaker.run(
                () -> restTemplate.getForObject(url, Product.class),
                throwable -> {
                    System.out.println(throwable.getMessage());
                    return new Product();});
    }
}
