# Assignment 3. Microservices (part I)

Assignments 3 and 4 will consist on implementing an (simple) application with microservices. In this assignment we will
build four microservices that will communicate in a synchronous request-response fashion (using REST). 

We will divide this assignment in two parts (weeks):
1. **First week**: Build the four microservices with the composite one communicating with the other three though a RestTemplate. Each microservice
   having its own H2 database
   
1. **Second week:** Put each microservice on its own Docker container. We will also have a MySql database manager on a Docker container and all
   microservices will use it as a database. In the real world, each microservice would connect to a different database manager
   but in order to save memory we will use a single database manager. Each microservie will use its own table with no references to the others.
   However, you could also explor if it feasible that each microservice connects to a different
   database within the manager (mysql> create database *microservice-name*)

## The MicrosSimple mini-example
In this example you'll find two services, one per module, where each module is a Spring Boot applications. 

### The services
There are two services: **product** and **front**. The former just reads and writes products to the database while the later just calls
the former using a restTemplate (Synchronous request-response communication).

### pom.xml files
You'll see that both microservices have the microsSimple as parent while microsSimple has the spring boot dependency as parent. It's just a bit different
from what we did with the modules in the Hexagonal exercise.

### Configuration files (application.yml)
Note that the **product** configuration file reads that it will use the port 7001 of the localhost and that it uses the 
default database (since it has the h2 dependency and no database is defined). It also defines a *docker* profile
where it states that it will use the port 8080 and that it uses a mysql database. We'll see that this database will run locally in a 
docker container.

The **front** configuration file also has a default profile that states the port 7000 of the localhost and host and port to find
the *product* service. It also has different port and host values for the *docker* profile.

### Docker
This example is also prepared to run in docker containers. Note that both **front** and **product** have a file called Dockerfile where it is 
defined how to run them in a docker container each.

The root project (MicrosSimple) also has a file called docker-compose.yml that is used to run the above mentioned containers and also
creats an additional one with the mysql database.

We'll got through these files in more detail below
   
## The Microservices (or what you need to do)
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

Recall that one way of reading these configurations is as follows (in the parameters of a bean constructor):
```Java
  @Value("${app.product-service.host}") String productServiceHost;
  @Value("${app.product-service.port}") int productServicePort;

  @Value("${app.recommendation-service.host}") String recommendationServiceHost;
  @Value("${app.recommendation-service.port}") int recommendationServicePort;

  @Value("${app.review-service.host}") String reviewServiceHost;
  @Value("${app.review-service.port}") int reviewServicePort;
```
## Docker(ize)
This is work for the second week of the exercise. We are going to create a docker image and a container for each of the microservices. Also
a image and container for the mysql database.

### Services' docker
A Docker file for each service that has the following
```
FROM openjdk:11
EXPOSE 8080
ADD ./target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```
Some things to take note of are as follows:
* We will base our Docker image on the official Docker image for OpenJDK and use the Java SE v11.
* We will expose port 8080 to other Docker containers.
* We add our fat-jar file to the Docker image from the Maven build library, ./target
* We will specify the command to be used by Docker when a container is started up using this Docker image, that is, *java -jar /app.jar*.

In order to build the image and run the container we can use the following commands in our terminal (example for the product service of the mini-example)
```bash
cd product 

# build the product microservice
mvn clear install

# build the docker image (-t gives a tag to the image)
docker build -t product . 

# list the images
docker images

# run the container: 
#    -p8080:8080 maps the container port to the localhost port. If we don't the container is not visible from the outside world
#    -d runs it deatached. The terminal is not blocked
docker run -d -p8080:8080 -e "SPRING_PROFILES_ACTIVE=docker" --name my-product-service product      

# see the logs of the container in the console: -f to keep seeing them while it runs
docker logs my-product-service -f

# to stop the container: -f to force remove the container even it's running 
docker rm -f  my-product-service 
```
Note that as it is, it would work because the docker profile of the service defines the database as a mysql one. If you want to see
it working you can remove the database configuration from the application.yml

### Docker compose
Since we don't want to build and run each container one by one we can use the docker compose to manage (build, run and stop) a set of 
services with a single command. The first thing we need is a **docker-compose.yml** file to define the containers we are going to use
(look for the file in the root of the example project). Another effect of having the containers in a single docker compose is that 
a network is created, and the containers are placed in the network so that they can reference to each other with their names.

In the file you'll see three definitions:
#### Product service
```
  my-product-service:
    build: product
    mem_limit: 350m
    environment:
      - SPRING_PROFILES_ACTIVE=docker
    depends_on:
      docker-mysql:
        condition: service_healthy
```
We can see that:
* Its name is my-product-service
* It builds the product image
* It limits the amount of memory it can use
* It defines the active profile named *docker*
* It can't be reached from outside the docker because its port is not mapped to the localhost
* It defines a depencency with the mysql container

#### Front service

```
  front:
    build: front
    mem_limit: 350m
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
```
We can see that:
* Its name is front
* It builds the front image
* It limits the amount of memory it can use
* It defines the active profile named *docker*
* It can be reached from outside the docker because its port is mapped to the localhost

#### mysql Container Service

```
  docker-mysql:
    image: mysql:5.7
    mem_limit: 350m
    ports:
      - "3306:3306"
    environment:
      - MYSQL_ROOT_PASSWORD=root
      - MYSQL_DATABASE=product
      - MYSQL_USER=user
      - MYSQL_PASSWORD=pwd
    healthcheck:
      test: ["CMD", "mysqladmin" ,"ping", "-uuser", "-ppwd", "-h", "localhost"]
      interval: 10s
      timeout: 5s
      retries: 10
    volumes:
        - my-datavolume:/var/lib/mysql
```
We can see that:
* Its name is docker-mysql
* It builds the image from an already created one (mysql:5.7)
* It limits the amount of memory it can use
* It can be reached from outside the docker because its port is mapped to the localhost (use for example the intellij database client. 
  Url jdbc:mysql://localhost:3306/product)
* A healthcheck is defined  
* It defines the volume where the data is stored and that is going to be mounted when the container is run

### Build and run the docker compose
In the following lines, using the terminal, we see respectively how to 
* build all the images for the containers
* run the containers (deatached)
* show the logs in the terminal 
* Stop and remove the containers

```bash
docker-compose build  
docker-compose up -d
docker-compose logs -f  
docker-compose down
```

### Defining tables and data in the mysql database  
The first time we use or run the mysql container we need to define the tables and maybe put some data in them. To do so once the mysql is up
and running we can:

```bash
# enter to the container
docker exec -it docker-mysql bash -l
```

In the bash prompt type
```bash
mysql -uroot -proot # to get into the mysql environment
use product; # or your database
```

And now in the database use sql to create the data:
```sql
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `name` varchar(45) NOT NULL,
    `description` varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO product (name, description) VALUES ('Soap','The finest soap you can face');
INSERT INTO product (name, description) VALUES ('After sun','When there is sun, there is aftersun');
```

