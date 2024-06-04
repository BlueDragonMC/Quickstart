# Required build contexts:
# - the default build context should be in the root of the Games project.
# - the `server` build context should point to the root of the Server project.

# Use the following command from the root of the project to build the image:
# docker build --build-context=server=../Server .

# Build the server and publish it to the local maven repository
FROM docker.io/library/gradle:8.6-jdk21 as build-server
COPY --from=server . /work
WORKDIR /work
RUN --mount=type=cache,target=/home/gradle/.gradle \
    /usr/bin/gradle --console=plain --info --stacktrace --no-daemon publishToMavenLocal

# Build the lobby and the specified game
FROM docker.io/library/gradle:8.6-jdk21 as build-games
ARG GAME

# Receive the built server from the previous step
COPY --from=build-server /root/.m2/repository/com/bluedragonmc /root/.m2/repository/com/bluedragonmc
COPY . /work
WORKDIR /work
RUN --mount=type=cache,target=/home/gradle/.gradle \
     /usr/bin/gradle --console=plain --info --stacktrace --no-daemon build

FROM docker.io/library/eclipse-temurin:21-jdk
ARG GAME

# Copy the final artifacts from both steps
COPY --from=build-server /work/build/libs/*-all.jar /server/server.jar
COPY --from=build-games /work/build/all-jars/*.jar /server/games/

WORKDIR /server
ENTRYPOINT ["java", "-jar", "server.jar"]