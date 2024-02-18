FROM eclipse-temurin:17
ARG TARGETPLATFORM
ARG BUILDPLATFORM
RUN echo "Building for $TARGETPLATFORM on $BUILDPLATFORM"
WORKDIR /workdir
COPY ./osrs-pathfinder .
RUN chmod +x gradlew && ./gradlew publishToMavenLocal --no-daemon && rm -rf ./*
