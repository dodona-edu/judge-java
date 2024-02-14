#!/usr/bin/env sh

# Test this Judge by running the integration tests in the docker image
image="dodona-java21:latest"

docker run \
    --mount type=bind,source=$PWD,destination=/home/runner/workdir,readonly \
    "$image" \
    -- ./integration-tests/run
