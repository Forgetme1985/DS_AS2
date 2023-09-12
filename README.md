# Distributed_System
1) Explanation and the steps to run the system:

In order to run the system, in the terminal run the commands below:

Run the aggregation server: java assignment.weather.AggregationServer 1234

we have one parameter for the aggregation server, this parameter tells us about the port that server is going to use

Run content servers: java assignment.weather.ContentServer http://localhost:1234 weather.txt

we have two parameters for the content server program, the first parameter tells us about the aggregation server address and port, 
the second parameeter is the path to the weather.txt file 

Test client: java assignment.weather.GETClient http://localhost:1234 

We have one paramenter for the client program which tells us about the aggregation server address and port 


2) There are some already shell files for testing:
   
AggregationServer.sh

ContentServer1.sh

ContentServer2.sh

Clients.sh


In order to run shell files in the mac terminal:


open the new terminal: sh AggregationServer.sh

open the new terminal: sh ContentServer1.sh

open the new terminal: sh ContentServer2.sh

open the new terminal: sh Clients.sh

note: ContentServer1.sh reading weather1.txt file whereas ContentServer2.sh reading weather2.txt

And also we have the design sketch file (v1.1) which describes the system



