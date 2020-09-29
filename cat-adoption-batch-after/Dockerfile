FROM maven:3.5-jdk-8 as MVN_BUILD

LABEL version 1.0 \
      description "Image for Cat Adoption Batch application"

COPY . /code
RUN echo 'building the application' > rm -Rf /code/target && \
	cd /code/ && mvn clean package

FROM openjdk:8-jre

COPY --from=MVN_BUILD /code/target/*.jar /app.jar
COPY --from=MVN_BUILD /code/entrypoint.sh /entrypoint.sh

RUN groupadd -r appuser && useradd -r -g appuser appuser
RUN chmod +x /entrypoint.sh
USER appuser

CMD ["/entrypoint.sh"]