package cat.tecnocampus.front.application;

import cat.tecnocampus.front.domain.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class ApplicationController {

    private final ReactiveCircuitBreakerFactory circuitBreakerFactory;

    private final WebClient webClient;

    private String productServiceUrl;

    public ApplicationController(@Value("${app.product-service.host}") String productServiceHost,
                                 ReactiveCircuitBreakerFactory circuitBreakerFactory,
                                 WebClient webClient)
    {
        productServiceUrl = "http://" + productServiceHost + "/products";
        this.circuitBreakerFactory = circuitBreakerFactory;
        this.webClient = webClient;
    }

    public void createProduct(Product product) {
        webClient.post()
                .uri(productServiceUrl)
                .body(Mono.just(product), Product.class)
                .retrieve().bodyToMono(String.class).block();
    }

    public List<Product> getProducts() {
        List<Product> products = webClient.get()
                .uri(productServiceUrl)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Product>>() {})
                .block();
        return products;
    }

    //@Retry(name="product")
    public Product getProduct(long id, int delay, int faultRatio) {
        ReactiveCircuitBreaker circuitBreaker = circuitBreakerFactory.create("product");
        var url = productServiceUrl + "/" + id + "?delay=" + delay + "&faultRatio=" + faultRatio;

        var product = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(Product.class)
                .transform(it -> circuitBreaker.run(it, throwable -> {
                    System.out.println(throwable.getMessage());
                    return Mono.just(new Product());
                }))
                .block();
        return product;
    }
}
