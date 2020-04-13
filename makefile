
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

SOURCES=$(shell find src -type f -name '*.java')
CLASSES_=$(subst src/,build/,$(SOURCES))
CLASSES=$(subst .java,.class,$(CLASSES_))
dist/judge.jar: $(CLASSES)
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
