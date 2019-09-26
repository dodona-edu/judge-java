
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

dist/judge.jar: build/dodona/junit/JUnitJSON.class \
				build/dodona/junit/MessageWriter.class \
				build/dodona/junit/TabTitle.class \
				build/dodona/util/Interactive.class \
				$(shell find build -type f)
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
run: dist/judge.jar lib/gson-2.8.1.jar lib/junit-4.13.jar lib/hamcrest-core-1.3.jar lib/system-rules-1.16.0.jar
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
