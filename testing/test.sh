#!/bin/sh

path_to_exercise="$1"

[ -z "$path_to_exercise" ] \
    && echo 'Provide a path to an exercise as first argument.' \
    && exit 1

allow_compilation_warnings="$(jshon -Q -e 'evaluation' -e 'allow_compilation_warnings' -u < "$path_to_exercise/config.json" || echo 'true')"
filename="$(jshon -e 'evaluation' -e 'filename' -u < "$path_to_exercise/config.json")"

find "$path_to_exercise/workdir/" -mindepth 1 -maxdepth 1 | xargs cp -r -t .

bash "../run" <<HERE
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
