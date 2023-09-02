FROM maven
WORKDIR /home

RUN curl -fsSL https://deb.nodesource.com/setup_16.x | bash -
RUN apt-get update
RUN apt-get install nodejs

ADD /code/GroceryListMaker/pom.xml /home
ADD /code/GroceryListMaker/domain/pom.xml /home/domain/pom.xml
ADD /code/GroceryListMaker/infrastructure/pom.xml /home/infrastructure/pom.xml
ADD /code/GroceryListMaker/application/pom.xml /home/application/pom.xml
RUN mvn verify --fail-never

ADD /code/GroceryListMaker/domain /home/domain
ADD /code/GroceryListMaker/application /home/application
ADD /code/GroceryListMaker/infrastructure /home/infrastructure
RUN npm install /home/infrastructure/src/main/resources/scripts/build

RUN mvn package -DskipTests=true
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/home/infrastructure/target/infrastructure-1.0-SNAPSHOT.jar"]
