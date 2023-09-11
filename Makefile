VERSION_NUMBER := 1.0

# Location of trees.
SOURCE_DIR  = ./Distributed_System/src/assignment/weather
OUTPUT_DIR  = ./Distributed_System/out/production/Distribute_System

#Java tools
JAVAC = javac
JFLAGS =  -d . -classpath . 
#Compile
sourcefiles = \
WeatherInformation.java\
LamportClock.java\
JsonParser.java\
WeatherConnection.java\
ContentServer.java\
GETClient.java\
AggregationServer.java


classfiles = $(sourcefiles:.java=.class)
all: $(classfiles)
%.class: $(SOURCE_DIR)/%.java
	#javac -d . -classpath  . $< 
	$(JAVAC) $(JFLAGS) $<

clean:
	rm -f *.class

