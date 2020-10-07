# Spring-Mysql-Redis
Spring Boot Mysql Redis REST API Cache example

### Prerequisites
- JDK 1.8
- Maven
- Mysql
- Redis (Using Lettuce)

## Quick Start

```
start MySQL service and create database
```

```
start Redis service
```

### Build
```
mvn clean package
```

### Run
```
java -jar target/spring-mysql-redis.jar
```

##
### Get information about system health, configurations, etc.
```
http://localhost:8091/env
http://localhost:8091/health
http://localhost:8091/info
http://localhost:8091/metrics
```


### Test using CURL

- data add
```
curl --location --request POST 'http://localhost:8080/api/employees' \
--header 'Content-Type: application/json' \
--data-raw '{
    "name" : "Yusran Fikri",
    "age"  : 10 ,
    "salary" : 2700 
}'
```

- data get cache expire 3000 ms
```
curl --location --request GET 'http://localhost:8080/api/employees/4'
```

- response : 
```
{
    "id": 4,
    "createdDate": "2020-10-07T11:37:39",
    "modifiedDate": "2020-10-07T11:37:39",
    "name": "Yusran Fikri",
    "age": 10,
    "salary": 2700.0
}
```


### Swagger-ui REST API Reference & Test
- http://localhost:8080/swagger-ui.html
- Response Content Type : application/json
