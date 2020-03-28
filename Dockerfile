FROM jenkins/jenkins:2.112
ADD target/applicant-service-0.0.1-SNAPSHOT.jar applicant-service.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar", "applicant-service.jar"]

