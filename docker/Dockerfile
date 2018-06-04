FROM spring:v1

USER root

EXPOSE 8018

WORKDIR /workspace/

COPY ./ /workspace/temp

RUN cd /workspace/temp \
    && mvn package \
    && mv target/ms-fruits-1.0-SNAPSHOT.jar /workspace/ \
    && cd /workspace/ \
    && rm -rf temp/

ENTRYPOINT ["java","-jar","-Dspring.profiles.active=pro","ms-fruits-1.0-SNAPSHOT.jar"]
