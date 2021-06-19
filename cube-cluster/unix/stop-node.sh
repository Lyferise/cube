#!/bin/bash

SIGNAL=${SIGNAL:-TERM}
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
cd "$SCRIPT_DIR" || exit 1

PROCESS=$(pgrep -a java | grep "$SCRIPT_DIR" | grep -v grep)
PID=$(echo "$PROCESS" | awk '{print $1}')

if [ -z "$PID" ]; then
  echo "No cube-cluster instance to stop"
  cd "$PWD" || exit 1
  exit 1
else
  kill -s "$SIGNAL" "$PID"
  cd "$PWD" || exit 1
  echo "STOPPED: $PROCESS"
fi