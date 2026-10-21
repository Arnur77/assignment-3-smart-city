QUICK START

Linux/macOS:
  ./run.sh

Windows PowerShell:
  Remove-Item -Recurse -Force out -ErrorAction SilentlyContinue
  New-Item -ItemType Directory out | Out-Null
  javac -d out (Get-ChildItem -Recurse src -Filter *.java | ForEach-Object { $_.FullName })
  java -cp out tests.TestRunner
  java -cp out benchmark.BenchmarkRunner
  python generate_plots.py
