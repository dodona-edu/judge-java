#!/bin/sh

# Builds dist/judge.jar from src/ in the judge image, reproducibly.
# With --check, fails if the committed dist/judge.jar differs from a fresh build.
set -e

image="${IMAGE:-ghcr.io/dodona-edu/dodona-java:latest}"

if [ "$1" != --in-image ]; then
    cd "$(dirname "$0")/.."
    [ "$1" = --check ] && mount=ro || mount=rw
    exec docker run --rm --platform linux/amd64 --user "$(id -u):$(id -g)" \
        --volume "$PWD":/judge:"$mount" \
        --workdir /judge \
        "$image" \
        devel/build-jar.sh --in-image "$@"
fi
shift

repo="$PWD"
build="$(mktemp -d)"
trap 'rm -rf "$build"' EXIT

find src -name '*.java' | LC_ALL=C sort > "$build/sources"
javac -classpath 'lib/*' -sourcepath src -d "$build/classes" @"$build/sources"

# Sorted entries and a fixed --date make the jar byte-for-byte reproducible.
cd "$build/classes"
find . -type f | LC_ALL=C sort > "$build/classes.list"
jar --create --file "$build/judge.jar" \
    --manifest "$repo/src/META-INF/MANIFEST.MF" \
    --date 1980-01-01T00:00:02Z \
    @"$build/classes.list"
cd "$repo"

if [ "$1" = --check ]; then
    if ! cmp -s "$build/judge.jar" dist/judge.jar; then
        echo "dist/judge.jar is out of date: run devel/build-jar.sh and commit dist/judge.jar." >&2
        exit 1
    fi
    echo "dist/judge.jar matches a fresh build of src/."
else
    mkdir -p dist
    cp "$build/judge.jar" dist/judge.jar
    sha256sum dist/judge.jar
fi
