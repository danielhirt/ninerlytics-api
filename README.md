# back-end-4155 v0.0.1
Upstream repository for back-end related code and resources.

## Current Backend Dependencies and Setup

The back-end to this project consists of a Java project with a Maven wrapper currently. 

In order to run the back-end, some setup is required:

1) If not already installed, install Apache Maven (Java build tool we are using): https://maven.apache.org/

2) If not already installed, install InfluxDB (v.1.7.8 download): https://portal.influxdata.com/downloads/

3) Run the InfluxDB server (if on Windows, navigate to install location and run influxd.exe)

4) Make sure you have the most recent back-end code. (Pull the upstream "develop" branch if not).

5) Open the back-end code as a Maven project (using Eclipse or something similar) and wait for resources to download. 

6) On the command line, change directory to the back-end code, then change directory to the "campusheatmap" folder.

7) Once in the "campusheatmap" directory, run `./mvnw install` 

8) Assuming you see `BUILD SUCCESS` after the above execution in the terminal, then run `./mvnw exec:java`.

9) The terminal will prompt you for a folder location that contains the data dump file(s). Provide this file path, without the filename, in the command line.

10) Assuming the previous steps have been successful, this is what you should have so far (with your own path to data dump): 

`Please Enter Data Dump Folder Location:`

`C:\Users\Daniel\Desktop\wifi-data-dumps`

`File Wi-Fi_Logs-9_16_19.txt Length of Files 1`

11) Depending on your system, the parsing and translation to InfluxDB can take a considerable amount of time (5+ minutes). After a minute or two, you should see something like `Batchpoint Written Point [name=users, time=1568607480000, tags={}, precision=MILLISECONDS, fields={Connections=237}]` executing rapidly in the command line, and a subsequent response from the InfluxDB server. 

12) After the operation is complete, the parsed data is now in InfluxDB!



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
			







