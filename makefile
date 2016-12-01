
JAVAC=javac -Xlint:all -classpath lib/*:build/ -d build/ -sourcepath src/
JAVA=java
all: dist/judge.jar

# ============================================================================ #
#                                   Sources                                    #
# ============================================================================ #

build/%.class: src/%.java
	$(JAVAC) $<

# ============================================================================ #
#                                  Packaging                                   #
# ============================================================================ #

dist/judge.jar: build/dodona/junit/JUnitJSON.class $(shell find build -type f)
	mkdir -p $(dir $@)
	jar -cf "$@" -C build/ .

# ============================================================================ #
#                                   Phonies                                    #
# ============================================================================ #

# something wicked
space :=
space +=
$(space) :=
$(space) +=

.PHONY: jar
jar: dist/judge.jar

.PHONY: run
run: dist/judge.jar lib/gson-2.8.1-SNAPSHOT.jar lib/junit-4.13.jar lib/hamcrest-core-1.3.jar
	$(JAVA) -cp .:$(subst $ ,:,$^) dodona.junit.JUnitJSON

#.PHONY: test
#test: dist/judge.jar
#	$(JAVA) -cp $< dodona.test.Test

.PHONY: clean
clean:
	rm -rf build/
	mkdir build/
	rm -rf dist/
	mkdir dist/
