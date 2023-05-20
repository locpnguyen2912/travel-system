# Travel System 
This Travel System via REST API allow user to process and create trips from touchData.csv and then export them as 3 files trips.csv, unprocessableTouchData.csv, and summary.csv in a zip file

## Assumption
* When creating trips from touch data, we identify unprocessed trips base on PAN number (missing, invalid data) then mark in reason column. 
* After that, we perform the filter to get the list for processed trips and unprocessed trips and write them to files


## Features implemented
* Travel System operations supported
  - allow the user to upload own touch data CSV file
  - process and create trips base on touch data input
  - export 3 files trips.csv, unprocessableTouchData.csv, summary.csv in zip file
* Unit test  
  
## Runtime requirements
* [JDK 11](https://jdk.java.net/11/) or later (JDK 8 is supported as well)

## Rest Endpoints
### Export files  
* __GET__ http://localhost:8080/api/trip/zip-export  
``curl --location --request GET 'http://localhost:8080/api/trip/zip-export' \
  --header 'Content-Type: application/zip' \
  --form 'file=@"path-of-your-file"'``
  
## Test data: 
* src/test/resources/test-data/touchData.csv

## Build and Run
```
mvn clean install
mvn spring-boot:run
```

## Testing
Input touchData.csv include list of records following scenario:
- 2 completed trips
- 1 incompleted trip
- 1 cancelled trip
- 4 unprocessed trips (missing pan, invalid pan)

![image](https://github.com/locpnguyen2912/travel-system/assets/6479274/6b056e0b-b6f6-4939-9c3f-d8763f6926ff)

## Result:

#### trips.csv

![image](https://github.com/locpnguyen2912/travel-system/assets/6479274/1641465b-ecc9-4a28-a037-b242dd8cdf40)

#### unprocessableTouchData.csv

![image](https://github.com/locpnguyen2912/travel-system/assets/6479274/ebac4670-b9e4-4ee0-bebc-11730cb3cc35)

#### summary.csv

![image](https://github.com/locpnguyen2912/travel-system/assets/6479274/47ca490d-e7b3-420d-8ac3-206816a93395)





