#!/bin/sh
set -e

# Usage: ./validate.sh <path-to-repo> [--overwrite]

if [ -z "$1" ]; then
    echo "Usage: $0 <path-to-repo> [--overwrite]"
    exit 1
fi

repo_path="$1"
shift

# Check if repo path exists
if [ ! -d "$repo_path" ]; then
    echo "Error: Directory '$repo_path' does not exist."
    exit 1
fi

# Determine if overwrite flag is passed
overwrite=""
if [ "$1" = "--overwrite" ]; then
    overwrite="--overwrite"
fi

# Run the integration test runner with the repo path
./integration-tests/run "$repo_path" $overwrite
