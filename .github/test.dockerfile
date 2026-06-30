# Image used to run the integration tests in CI.
#
# The judge runs on the dodona-java image (published by dodona-edu/docker-images),
# which is a minimal Alpine shipping only what the judge needs at runtime. The
# test harness (integration-tests/run) additionally needs bash, so we layer it on
# here rather than bloating the published image. See dodona-edu/dodona#7583.
FROM ghcr.io/dodona-edu/dodona-java:latest
USER root
RUN apk add --no-cache bash
USER runner
