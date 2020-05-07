#!/usr/bin/env python3

import sys
import os
import json
import shutil
import subprocess
import shlex

path_to_exercise = sys.argv[1]
assert path_to_exercise 
assert os.path.exists(path_to_exercise) 
assert os.path.isdir(path_to_exercise)

print("parsing config.json")

exercise_config = open(os.path.join(path_to_exercise, "config.json"))
assert exercise_config

exercise_config_json = json.load(exercise_config)

allow_compilation_warnings = False
if "allow_compilation_warnings" in exercise_config_json["evaluation"]:
    allow_compilation_warnings = exercise_config_json["evaluation"]["allow_compilation_warnings"]

filename = exercise_config_json["evaluation"]["filename"]

print("Copying workdir files")

workdir = os.path.join(path_to_exercise, "workdir")
if os.path.exists(workdir):
    assert os.path.isdir(workdir)
    shutil.copytree(workdir, os.getcwd(), dirs_exist_ok=True)

run_options = {
    "resources": os.path.join(path_to_exercise, "evaluation"),
    "judge": "..",
    "natural_language": "en",
    "workdir": os.getcwd(),
    "allow_compilation_warnings": allow_compilation_warnings,
    "filename": filename,
    "time_limit": 30,
    "memory_limit": 100000000,
    "source": os.path.join(path_to_exercise, "solution", filename)
}

executable = os.path.join("..", "run")
assert os.path.exists(executable)
assert os.path.isfile(executable)

print("calling command:", executable)
print("with argument  :", json.dumps(run_options, indent=4))
print("output         :")
print()

subprocess.run(executable, input=json.dumps(run_options).encode('utf-8'))