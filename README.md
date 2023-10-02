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

2) The description of LamportClock implementation:

In the LamportClock class: 
-we have a counter which used to keep the order of sending and receving messages correct.
-we have an increase counter method to update the counter.
-we have an updateReceive method to check if we need to update the counter.

Either the aggregation server, the client or the content server has a lamportClock instance.

The implementation is:

step 1: the content server send a put message into the aggregation server; it also attachs the current value of its  lamport clock counter after it increases the counter.
step 2: the aggregtion server will compare its counter value (b) with the content server's lamport clock counter value (a)
	2a) if b > a then do nothing.
	2b) if b < a then b = a
step 3: the aggregation server will increase b before it attachs b into the response message and send it to the aggregation server.
step 4: the content server will do the same the comparision:
	4a) if a > b then do nothing
	4b) if b > a then update a = b

the same steps above for the client with a get message.

**: please take a look at LamportClock_Implementation.pdf for more details


3) There are some already shell files for testing:
   
AggregationServer.sh

ContentServer.sh (this file will run two content servers at the same time)

Clients.sh


In order to run shell files in the mac terminal:



open the new terminal: sh AggregationServer.sh

open the new terminal: sh ContentServer.sh


open the new terminal: sh Clients.sh

note: two content servers: one reads weather1.txt file and the other reads weather2.txt

And also we have the design sketch file (v1.1) which describes the system



