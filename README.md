# Travel System 
This Travel System via REST APIs allowing you to process and create trips from touchData.csv then export them as 3 files trips.csv, unprocessableTouchData.csv, summary.csv in zip file 

## Assumption
* When creating trips from touch data, we identify unprocessed record base on PAN number (missing, invalid data) then mark in reason column. 
* After that, we perform the filter to get the list for processed record and unprocessed record and write them to files


## Features implemented
* Travel System operations supported
  - allow user to upload their touch data csv file
  - process and create trips base on touch data input
  - export 3 files trips.csv, unprocessableTouchData.csv, summary.csv in zip file
* Unit test  
  
## Runtime requirements
* [JDK 11](https://jdk.java.net/11/) or later (JDK 8 is supported as well)

### Rest Endpoints
#### Export files  
* __GET__ http://localhost:8080/api/trip/zip-export  
``curl --location --request GET 'http://localhost:8080/api/trip/zip-export' \
  --header 'Content-Type: application/zip' \
  --form 'file=@"path-of-your-file"'``
  
### Test data: 
* tmp/touchData.csv

### Build and Run
```
mvn clean install
mvn spring-boot:run
```

