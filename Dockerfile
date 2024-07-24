FROM amazoncorretto:19 AS BUILD_STAGE

ENV APP_HOME=/app/
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

# Now actually copy the source and build, this should reuse previously cached layers
# on subsequent builds as well as restored .gradle cache.
COPY . .
RUN ./gradlew :test-code-quality-tools-api:build -x :test-code-quality-tools-api:test

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
