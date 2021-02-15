# Assignment 3. Microservices (part I)

Assignments 3 and 4 will consist on implementing an (simple) application with microservices. In this assignment we will
build four microservices that will communicate in a synchronous request-response fashion (using REST). 

We will divide this assignment in two parts (weeks):
1. **First week**: Build the four microservices with the composite one communicating with the other three though a RestTemplate. Each microservice
   having its own H2 database
   
1. **Second week:** Put each microservice on its own Docker container. We will also have a MySql database manager on a Docker container and all
   microservices will use it as a database. In the real world, each microservice would connect to a different database manager
   but in order to save memory we will use a single database manager. However, each microservice will connect to a different
   database within the manager (mysql> create database *microservice-name*)
   
## The Microservices
We will have four microservices. One to manage **products**, another no manage product **reviews**, and a third one to manage 
product **recommendations**. The last one is going to **compose** the product: product + reviews + recommendations

### Product Service
The product service manages product information and describes each product with the following attributes:
* Product ID
* Name
* Description
* Weight

The service will provide methods for:
* List all products
* List a product given its id
* Create a product
* Delete a product given its id

### Review Service 
The review service manages product reviews and stores the following information about each review:
* Product ID
* Review ID
* Author
* Subject
* Content

The service will provide methods for:
* List product's review given its id (of the product)
* Create a review (for a product)
* Delete all product's reviews given its id (of the product)

### Recommendation Service
The recommendation service manages product recommendations and stores the following information about each recommendation:
* Product ID
* Recommendation ID
* Author
* Rate
* Content

The service will provide methods for:
* List product's recommendation given its id (of the product)
* Create a recommendation (for a product)
* Delete all product's recommendations given its id (of the product)

### Product Composite Service
Product composite service
The product composite service aggregates information from the three core services and presents information about a product as follows:
* Product information, as described in the product service
* A list of product reviews for the specified product, as described in the review service
* A list of product recommendations for the specified product, as described in the recommendation service

The service will provide methods for:
* List all product-composites
* List a product-composite given the product id
* Create a product-composite
* Delete a product-compsite given the product id

Note that this service in order to create, delete or list product composites needs to call the other three services.

### Infrastructure
Since, at this stage, we don't have any service discovery mechanism in place, we will use hardcoded port numbers for each microservice. 
We will use the following ports:
* Product composite service: 7000
* Product service: 7001
* Review service: 7002
* Recommendation service: 7003

We will get rid of the hardcoded ports later when we start using Docker
  
For the composite service to know where to connect with the other services, its *application.yml* file will have the following
```
server.port: 7000

app:
    product-service:
        host: localhost
        port: 7001
    recommendation-service:
        host: localhost
        port: 7002
    review-service:
        host: localhost
        port: 7003
```

Recall that one way of reading these configurations is as follows:
```Java
  @Value("${app.product-service.host}") String productServiceHost;
  @Value("${app.product-service.port}") int productServicePort;

  @Value("${app.recommendation-service.host}") String recommendationServiceHost;
  @Value("${app.recommendation-service.port}") int recommendationServicePort;

  @Value("${app.review-service.host}") String reviewServiceHost;
  @Value("${app.review-service.port}") int reviewServicePort;
```
## Docker(ize)
This is work for the second week

### Service's docker
A Docker file for each service

### Database docker
