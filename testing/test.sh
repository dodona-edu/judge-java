#!/bin/sh

path_to_exercise="$1"

[ -z "$path_to_exercise" ] \
    && echo 'Provide a path to an exercise as first argument.' \
    && exit 1

allow_compilation_warnings="$(jq -r '.evaluation.allow_compilation_warnings == true' "$path_to_exercise/config.json")"
filename="$(jq -r '.evaluation.filename' "$path_to_exercise/config.json")"

[ -d "$path_to_exercise/workdir/" ] && find "$path_to_exercise/workdir/" -mindepth 1 -maxdepth 1 | xargs cp -r -t .

sh "../run" <<HERE
{
    "resources": "$path_to_exercise/evaluation",
    "judge": "..",
    "workdir": "$(pwd)",
    "allow_compilation_warnings": "$allow_compilation_warnings",
    "filename": "$filename",
    "time_limit": 30,
    "memory_limit": 100000000,
    "source": "$path_to_exercise/solution/$filename"
}
HERE

echo
