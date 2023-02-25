FROM maven
WORKDIR /home

RUN curl -fsSL https://deb.nodesource.com/setup_16.x | bash -
RUN apt-get update
RUN apt-get install nodejs

ADD pom.xml /home
ADD domain/pom.xml /home/domain/pom.xml
ADD infrastructure/pom.xml /home/infrastructure/pom.xml
ADD application/pom.xml /home/application/pom.xml
RUN mvn verify --fail-never

ADD domain /home/domain
ADD application /home/application
ADD infrastructure /home/infrastructure
RUN npm install /home/infrastructure/src/main/resources/scripts/build

RUN mvn package -DskipTests=true
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/home/infrastructure/target/infrastructure-1.0-SNAPSHOT.jar"]
