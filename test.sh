#!/usr/bin/env sh

# Test this judge by running the integration tests in the judge image, same as CI.
docker build --tag dodona-java-test --file .github/test.dockerfile .
docker run --rm \
    --volume "$PWD":/judge:ro \
    --workdir /judge \
    dodona-java-test \
    ./integration-tests/run -v
