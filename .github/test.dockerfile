FROM ghcr.io/dodona-edu/dodona-java:latest
USER root
RUN apk add --no-cache bash
USER runner
