all: jar

.PHONY: jar
jar:
	./devel/build-jar.sh

.PHONY: check
check:
	./devel/build-jar.sh --check
