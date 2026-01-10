FROM eclipse-temurin:17-jre-alpine
WORKDIR /orderqty
COPY target/*.jar orderqty.jar
EXPOSE 8093
ENTRYPOINT ["java","-jar","orderqty.jar"]