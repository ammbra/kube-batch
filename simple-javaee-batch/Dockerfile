FROM maven:3.5-jdk-8 as MVN_BUILD

LABEL version 1.0 \
      description "Image for Simple JavaEE Batch application"

COPY . /code
RUN echo 'building the application' > rm -Rf /code/target && \
	cd /code/ && mvn clean package

FROM openliberty/open-liberty:kernel-java8-openj9-ubi

COPY --chown=1001:0 --from=MVN_BUILD /code/target/*.war /config/dropins/
COPY --chown=1001:0 --from=MVN_BUILD /code/src/main/liberty/config/server.xml /config/

RUN configure.sh