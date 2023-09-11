VERSION_NUMBER := 1.0

# Location of trees.
SOURCE_DIR  = ./src/assignment/weather
OUTPUT_DIR  = ./out/production/Distribute_System

#Java tools
JAVAC = javac
JFLAGS =  -d . -classpath . 
#Compile
sourcefiles = \
AggregationServer.java\
ContentServer.java\
GETClient.java\
JsonParser.java\
LamportClock.java\
WeatherConnection.java\
WeatherInformation.java


classfiles = $(sourcefiles:.java=.class)
all: $(classfiles)
%.class: $(SOURCE_DIR)/%.java
	#javac -d . -classpath  . $< 
	$(JAVAC) $(JFLAGS) $<

clean:
	rm -f *.class

