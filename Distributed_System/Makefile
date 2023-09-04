VERSION_NUMBER := 1.0

# Location of trees.
SOURCE_DIR  = ./src/assignment/calculator
OUTPUT_DIR  = ./out/production/Distribute_System

#Java tools
JAVAC = javac
JFLAGS =  -d . -classpath . 
#Compile
sourcefiles = \
Calculator.java\
CalculatorImplementation.java\
CalculatorServer.java\
CalculatorClient.java


classfiles = $(sourcefiles:.java=.class)
all: $(classfiles)
%.class: $(SOURCE_DIR)/%.java
	#javac -d . -classpath  . $< 
	$(JAVAC) $(JFLAGS) $<

clean:
	rm -f *.class

