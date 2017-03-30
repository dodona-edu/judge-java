#!/bin/bash

path_to_exercise="$1"

[ -z "$path_to_exercise" ] \
    && echo 'Provide a path to an exercise as first argument.' \
    && exit 1

filename="$(jshon -e 'evaluation' -e 'filename' -u < "$path_to_exercise/config.json")"

find "$path_to_exercise/workdir/" -mindepth 1 -maxdepth 1 | xargs cp -r -t .

bash "../run" <<HERE
{
    "resources": "$path_to_exercise/evaluation",
    "judge": "..",
    "workdir": "$(pwd)",
    "filename": "$filename",
    "time_limit": 30,
    "memory_limit": 100000000,
    "source": "$path_to_exercise/solution/$filename"
}
HERE

