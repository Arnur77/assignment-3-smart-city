#!/usr/bin/env bash
set -e
rm -rf out
mkdir -p out
javac -d out $(find src -name '*.java')
java -cp out tests.TestRunner
java -cp out benchmark.BenchmarkRunner
python3 generate_plots.py
