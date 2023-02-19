FROM maven
WORKDIR /home

ADD pom.xml /home
ADD domain/pom.xml /home/domain/pom.xml
ADD infrastructure/pom.xml /home/infrastructure/pom.xml
ADD application/pom.xml /home/application/pom.xml
RUN mvn clean verify --fail-never

ADD domain /home/domain
ADD infrastructure /home/infrastructure
ADD application /home/application

RUN mvn clean package -DskipTests=true
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/home/infrastructure/target/infrastructure-1.0-SNAPSHOT.jar"]
