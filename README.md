# Wifi Data Visualization and Analytics Tool
Upstream repository for back-end related code and resources.

Documentation Author: Daniel Hirt, dhirt@uncc.edu

## Initial Setup & Dependencies

API Development Version: **v1.0.2-SPRT-1**

API Deployment Version:  **v1.0.2-PROD**

"campusheatmap" is **DEPRECATED**

#### Dependencies

The back-end to this project consists of a Springboot/MVC Java API with a Maven wrapper which interfaces with data in a time-series database:

* [Spring Boot](https://spring.io/projects/spring-boot "Spring Boot")

* [Apache Maven](https://maven.apache.org/ "Maven")

* [InfluxDB v1.7.8](https://portal.influxdata.com/downloads/ "Influx")


#### Initial Development Environment Setup

In order to run the back-end, some setup is required:

1) If not already installed, install Apache Maven (Java build tool we are using): https://maven.apache.org/

2) If not already installed, install InfluxDB (v.1.7.8 download): https://portal.influxdata.com/downloads/

3) Run the InfluxDB server (if on Windows, navigate to install location and run influxd.exe).

4) Make sure you have the most recent back-end code. (Pull the upstream "develop" branch if not).

5) Import the back-end code as a Maven project (using Eclipse or something similar) and wait for the resources to download. 

![import-image](/back-end-4155/setup-images/import.png)

6) On the command line, change directory to the back-end code (back-end-4155), then change directory to the "api-4155" folder.

7) Once in the "api-4155" directory, run `./mvnw install spring-boot:run`. This will build the project and attach the Spring runner, and launch an embedded Tomcat server on port 8080.

8) The terminal will prompt you for a folder location that contains the data dump file(s). Provide this file path, without the filename, in the command line.

9) Assuming the previous steps have been successful, this is what you should have so far (with your own path to data dump): 

![run-image](/back-end-4155/setup-images/run.jpg)

10) Depending on your system, the parsing and translation to InfluxDB can take a considerable amount of time (5+ minutes). Please be patient here, even if it doesn't look like anything is happening... it is indeed processing the data. 

If you see no errors from the API or Influx, eventually the data will make it's way there. Sooner or later, you should see something like this

`Batchpoint Written Point [name=connectionsByBuilding, time=1568607480000, tags={Building}, precision=MILLISECONDS, fields={Connections=237}]` 

executing rapidly in the terminal, and a subsequent response from the InfluxDB server terminal. 

11) After the operation is complete, the parsed data is now in InfluxDB! (It is okay that nothing else is printed from the API, assuming there were no errors it is indeed still running!)

12) You can verify this newly parsed data is InfluxDB by running the Influx CLI (influx.exe):

![influx-image](/back-end-4155/setup-images/influx.jpg)

The above query selects all of the connection data we have on Atkins. This can be done for any of the buildings we have parsed (can find this list in the API).

## Important Setup Notes & Troubleshooting

- There are still cases, despite some error handling, in which the API will fail completely in it's current state. Most of these are commented out, or not accessable from the front-end.

- If the API fails, in most cases, it is not necessary to restart the Angular development server. However, it is best to relaunch the API using `./mvnw install spring-boot:run`.

- On every launch, the API will ask if there is new data to parse. Assuming you have already parsed the data, and have InfluxDB running, enter "n" here. Otherwise, the API will ask for the log file and try to overwrite the current database. 

- If you edit and save any code changes on the API while it is running, Spring will automatically detach and attempt to relaunch the project. In this case, the connection with InfluxDB will be interrupted and an exception will be thrown. With that being said, it is essential to relaunch after any edits, again using `./mvnw install spring-boot:run` and entering "n" to the question of adding/parsing new data. 

#### Troubleshooting Connection Issues

Assuming you have properly configured InfluxDB (message myself or Matt W. if you have issues with this step) then running the InfluxDB server should always be the first step before running the rest of the codebase. You can find an example of what the `influxdb.conf` file and your InfluxDB install directory should look like in our Google Drive: 

* [InfluxDB Configuration](https://drive.google.com/drive/u/1/folders/1TUWBTW1dJLBar8XtnFVuzERszly3-Hyl "InfluxDB Config")

By default, the InfluxDB server tries to run on port 3000. On Windows machines, this port is usually restricted. To avoid this issue entirely, it is recommended to run the InfluxDB on an available port. The API currently is set to connect to Influx on port 8086.

#### Port Configuration

The project codebases are set to run on the following ports for local development:

* InfluxDB Server: http://localhost:8086

* Springboot API: http://localhost:8080

* Angular Dev Server: http://localhost:4200

## API Endpoint Details 

(Work in progress, will continue to document as sprints progress)

Springboot provides an easy framework for building web controllers to accept requests from a client and return a response. The API has several working endpoints which execute queries against InfluxDB, returning a response object containing JSON for use by the Angular client. 

The API recieves requests through a default gateway, as configured in the Angular client:

`{address}:8080/api/v1/{endpoint}`

There is a separate entry point for the file upload/download functions: 

`{address}:8080/api/v1/files/{endpoint}`

Request Flow: Angular client --> API Gateway --> Web Controller --> Service(s) --> Web Controller --> JSON --> Angular client

##### Utilization Data by Building

HTTP Method: GET
Description: Returns a list of point objects, each containing connection, disconnection, building, and date/time data. 

```
@GetMapping("/connectionsByBuilding/b={building}")
private ResponseEntity<List<UsersPoint>> getConnectionDataByBuilding(@PathVariable String building)

```
Path Variable `String {building}`: Where "building" is any of the campus buildings.

For example, to query data for Atkins we pass the string 'Atki' to the service layer in the Angular client, which results in the following GET request:

`/connectionsByBuilding/b=Atki` 

This will return JSON objects with the following structure: 

```
[
    {
        "time": 1568606400.000000000,
        "building": "Atki",
        "connections": 80,
        "disconnections": 35,
        "dateAndTime": "2019 Sep 16 00:00"
    },
    {
        "time": 1568606460.000000000,
        "building": "Atki",
        "connections": 52,
        "disconnections": 25,
        "dateAndTime": "2019 Sep 16 00:01"
    },
    {
        "time": 1568606520.000000000,
        "building": "Atki",
        "connections": 69,
        "disconnections": 30,
        "dateAndTime": "2019 Sep 16 00:02"
    },
    {
        "time": 1568606580.000000000,
        "building": "Atki",
        "connections": 71,
        "disconnections": 32,
        "dateAndTime": "2019 Sep 16 00:03"
    },

```
#### Total Utilization Data

TBD



Let me know if there is anything that is unclear, or if I can aid in the setup process should you run into any issues. Thanks for reading!

- Daniel

## Reference Links and Documentation

* [Agile Board & User Stories](https://docs.google.com/spreadsheets/d/1dm9sP_mIdLl37zeNOCKmncDMO0HoIxjeewmiFUyAlhI/edit?usp=sharing "Agile Board")


* [Project Proposal & Problem Statement](https://docs.google.com/presentation/d/1fxfAZ-zVOSKzFW1SE5DlYFFz0uJsrnEZB4Wij7uvN5M/edit?usp=sharing "Proposal")


* [Git Process & Project Architecture](https://docs.google.com/document/d/1HAwwUEqxKyuCf5BHwdLaAdD1ZKnvuG_bWOq9BIKS4ek/edit?usp=sharing "Project Management")

## Team and Roles

* Daniel Hirt: Scrum Master, Back-end Development, UI/UX Development	
* Carson Leedy: Database Development, UI/UX Development
* Austin Young: UI/UX Development, Front-end Development	
* Abhinav Kasu: Back-end Development
* Matthew Walter: Back-end Development
* Aaron Yow: Back-end Development
* Andre Raposo: Back-end Development
* Seth Frady: Front-end Development			
* Aasim Munshi: Front-end Development				
* Matthew Shangle: Front-end Development			
			







