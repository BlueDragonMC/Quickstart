# BlueDragon Quickstart

Welcome! This project will get you up and running with BlueDragon's server software.

## Instructions

Prerequisites:

* Java 21 or later installed
* [Docker](https://www.docker.com/get-started/) installed and running

1. Clone this repo and the Server repo:
   ```bash
   git clone https://github.com/BlueDragonMC/Quickstart
   git clone https://github.com/BlueDragonMC/Server
   ```

2. Install and start MongoDB and LuckPerms

   The easiest way to do this is with [Docker](https://www.docker.com/).
   Once you have Docker installed, follow the Creating a Network, MongoDB,
   and LuckPerms sections of [this guide](https://developer.bluedragonmc.com/deployment/docker/#running).

3. Run the Gradle task:
   ```bash
   ./gradlew runDev
   ```
   _(on Windows, it would be `gradlew.bat runDev`)_

   This will install dependencies, build the project, and start up an instance
   of the BlueDragon core server with your game plugin preinstalled.
   Whenever you make a change, rerun the Gradle task and your server will
   restart with the updated plugin.

4. You should see the server appear in your multiplayer menu as a LAN world.
   If not, join `localhost`.

## Building and Deploying

Running this server in production requires a few additional considerations,
but it allows you to run multiple game servers that coordinate together using
an external service ([Puffin](https://github.com/BlueDragonMC/Puffin)). For
more information on what services BlueDragon runs,
see [this section](https://developer.bluedragonmc.com/intro/project-structure/#services-and-their-jobs)
of our docs.

**To learn how to deploy BlueDragon systems on baremetal, Docker, or Kubernetes,
see [these guides](https://developer.bluedragonmc.com/choose-deployment-method/).**