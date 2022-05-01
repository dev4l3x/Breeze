FROM maven
WORKDIR /home
COPY . .
RUN mvn clean package -DskipTests=true
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/home/infrastructure/target/infrastructure-1.0-SNAPSHOT.jar"]
