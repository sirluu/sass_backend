## JTX-SERVICES

## Build and deploy
# Gradle Profiles
There are totally 3 environments: stg, dev, prod.
* ***Staging***
    ```
    $ gradle profileSetup -Penvironment=stg clean build -x test
    ```
* ***Development***
    ```
    $ gradle profileSetup -Penvironment=dev clean build -x test
    ```
* ***Production***
    ```
    $ gradle profileSetup -Penvironment=prod clean build -x test
    ```
# Complete steps to run the project

* Run the application by clicking run command or by running the jar created.

    * **Run Command**
        ```
        java -jar /stockholm-service/build/libs/stockholm-service.jar --host 0.0.0.0
		```
     * **Cron job**
        ```
        + Create a script file with content post data: curl -X POST "http://localhost:8888/stockholm/synchronize" -H "accept: application/json" -H "Content-Type: application/json" -d "{ "processMode": "string\", \"startMode\": \"string\", \"startTime\": \"string\"}"
		+ Setup schedule on client OS machine
		```
     * **Manual Job**
        ```
        + Use tools or swagger-ui, cmd ...
        ```
## Docker setup introduction
## Prepare sources

# Clone and checkout
* git clone https://github.com/JapanTaxi/stockholm-serivce.git
* git checkout feature/CJ-102-MultiProject

# Build jar
* chmod +x buildjar.sh
* ./buildjar.sh

# Build all
* docker-compose up -d --no-deps --build

# Build single
* cd singapore-service

* docker-compose up -d --no-deps --build

* cd stockholm-service

* docker-compose up -d --no-deps --build
