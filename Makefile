JAVAC=javac
JVM=java
CLASSPATH = lib/iris-0.60.jar:lib/iris-parser-0.60.jar:src/
sources = $(wildcard src/iris/*.java)
classes = $(sources:.java=.class)
current_dir = $(shell pwd)

all: compile

compile: $(classes)

clean :
	rm -f $(classes)

%.class : %.java
	$(JAVAC) -cp $(CLASSPATH) $<

