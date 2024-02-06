FROM eclipse-temurin:17
WORKDIR /workdir
COPY ./osrs-pathfinder .
RUN chmod +x gradlew && ./gradlew publishToMavenLocal --no-daemon && rm -rf ./*
