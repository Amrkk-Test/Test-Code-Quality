FROM amazoncorretto:19 AS BUILD_STAGE

ENV APP_HOME=/app/
ENV SONARQUBE_URL=http://localhost:9000

RUN mkdir -p $APP_HOME/test-code-quality-tools-api/src/main/java
RUN mkdir -p $APP_HOME/test-code-quality-tools-api/src/main/resources/graphql
RUN mkdir -p $APP_HOME/newrelic

WORKDIR $APP_HOME

ARG GITHUB_TOKEN
ARG NEW_RELIC_LICENSE_KEY
ARG NEW_RELIC_APP_NAME

# This part ensures that Gradle pulls dependencies which will be cached by docker
# speeding up subsequent Docker builds.
COPY test-code-quality-tools-api/src/main/resources/graphql ./test-code-quality-tools-api/src/main/resources/graphql
COPY test-code-quality-tools-api/build.gradle ./test-code-quality-tools-api/

COPY gradle gradle
COPY gradlew settings.gradle gradle.properties ./

COPY newrelic ./newrelic


RUN ./gradlew :test-code-quality-tools-api:build -x :test-code-quality-tools-api:bootJar -x :test-code-quality-tools-api:test --continue

RUN yum update -y && yum install -y curl unzip

#RUN curl -sSL -o sonar-scanner-cli.zip https://binaries.sonarsource.com/Distribution/sonar-scanner-cli/sonar-scanner-cli-4.7.1.11002-linux.zip && unzip sonar-scanner-cli.zip && mv sonar-scanner-4.7.1.11002-linux /opt/sonar-scanner && rm sonar-scanner-cli.zip

COPY . .
RUN ./gradlew :test-code-quality-tools-api:build -x :test-code-quality-tools-api:test


# Run Sonar analysis
RUN ./gradlew sonar -Dsonar.projectKey=Test-Code-Quality -Dsonar.projectName='Test Code Quality' -Dsonar.host.url=http://172.17.0.2:9000 -Dsonar.login=squ_c25529979ca49f8185910b8af6230bc9610ec91b


# Create tiny distroless Java image using only the .jar from the builder
# For debugging, use a full image:
FROM amazoncorretto:19
WORKDIR /app

ENV NEW_RELIC_APP_NAME=${NEW_RELIC_APP_NAME}
ENV NEW_RELIC_LICENSE_KEY=${NEW_RELIC_LICENSE_KEY}
ENV NEW_RELIC_ARG="-javaagent:/app/newrelic.jar"
ENV JAVA_OPTS="$JAVA_OPTS $NEW_RELIC_ARG"
# run an arbitrary command to force Docker to actually use cache
CMD ls
COPY --from=BUILD_STAGE /app/newrelic /app
COPY --from=BUILD_STAGE /app/test-code-quality-tools-api/build/libs/ /app
EXPOSE 8080

CMD java $NEW_RELIC_ARG -jar "/app/test-code-quality-tools-api.jar"
