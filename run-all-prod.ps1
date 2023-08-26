# Build the Spring Boot application using Maven
mvn clean package
# build image
docker build -t prod-accounts:latest .
# Run the Docker container
docker run --rm --name prod-accounts -it --network dockerNetwork -p 8081:8081 prod-accounts:latest

