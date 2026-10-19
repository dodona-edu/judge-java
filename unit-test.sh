#!/usr/bin/env sh

# Run this judge's unit tests in the judge image, same as CI.
# On an arm64 host without an arm64 image, set DOCKER_DEFAULT_PLATFORM=linux/amd64.
docker run --rm \
    --volume "$PWD":/judge:ro \
    --workdir /judge \
    "${IMAGE:-ghcr.io/dodona-edu/dodona-java:latest}" \
    ./unit-tests/run
