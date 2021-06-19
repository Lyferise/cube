#!/bin/bash

# ./node-cluster.sh <command> <options>
# commands:
#   create <cluster_name> <number_of_nodes>
#   status <cluster_name> [node_id]
#   start <cluster_name> [node_id]
#   stop <cluster_name> [node_id]

PORT_BASE=7000
NODE_PREFIX="node"
CFG_DIR="conf"
CFG_FILE="application.yml"
STARTUP_TIMEOUT=5 #seconds
SIGNAL=${SIGNAL:-TERM}

# Which java to use
if [ -z "$JAVA_HOME" ]; then
  JAVA="java"
else
  JAVA="$JAVA_HOME/bin/java"
fi

print_usage() {
  echo "Usage $1 <command> <options>"
  echo "commands:"
  echo "    create <cluster_name> <number_of_nodes>"
  echo "    status <cluster_name> [node_id]"
  echo "    start <cluster_name> [node_id]"
  echo "    stop <cluster_name [node_id]"
}

######## CREATE COMMAND ########
handle_create_command() {
  validate_create_arguments "$1" "$2"
  create_dir_structure "$1" "$2"
}

validate_create_arguments() {
  if [ -z "$1" ] | [ -z "$2" ]; then
    echo "Error: missing argument(s)"
    print_usage "$0"
    exit 1
  fi

  if ! [[ "$2" =~ ^[0-9]+$ ]]; then
    echo "Error: non-integer argument"
    print_usage "$0"
    exit 1
  fi
}

create_dir_structure() {
  mkdir "$1"
  declare -a nodeCfgs
  declare -a nodeIds
  declare -a nodePorts
  echo "CREATING:"
  for i in $(seq 1 "$2"); do
    nodeId=$(make_node_id "$i")
    nodeName="$NODE_PREFIX$nodeId"
    nodeDir="./$1/$nodeName"
    make_node_dir_structure "$nodeDir"
    copy_script "$nodeDir"
    copy_config "$nodeDir"
    modify_config "$nodeDir" "$nodeId" $(("$PORT_BASE" + "$i"))
    copy_binary "$nodeDir"
    nodeCfgs+=("$nodeDir/$CFG_DIR/$CFG_FILE")
    nodeIds+=("$nodeId")
    nodePorts+=($(("$PORT_BASE" + "$i")))
    echo "$nodeDir"
  done

  for dir in "${nodeCfgs[@]}"; do
    printf "cluster:\n" >>"$dir"
    for idx in $(seq 0 $((${#nodeIds[@]} - 1))); do
      {
        printf "  -\n"
        printf "    nodeId: %s\n" "${nodeIds[$idx]}"
        printf "    address: ws://localhost:%s\n" "${nodePorts[$idx]}"
      } >>"$dir"
    done
  done
  echo "DONE"
}

make_node_id() {
  newId=$((1782 + 913 * ($1 - 1)))
  echo "$newId"
}

make_node_dir_structure() {
  mkdir "$1"
  mkdir "$1/$CFG_DIR"
  mkdir "$1/bin"
  mkdir "$1/log"
  mkdir "$1/data"
}

copy_script() {
  cp ./start-node.sh "$1"
  cp ./stop-node.sh "$1"
}

copy_config() {
  cp "../$CFG_DIR/$CFG_FILE" "$1/$CFG_DIR"
}

modify_config() {
  CFG_PATH="$1/$CFG_DIR/$CFG_FILE"
  sed -i "s/nodeId: 1/nodeId: $2/g" "$CFG_PATH"
  sed -i "s/port: \([0-9]\+\)/port: $3/g" "$CFG_PATH"
}

copy_binary() {
  cp "../build/libs/"*".jar" "$1/bin"
}

add_cluster_info() {
  echo "$1/$2/$CFG_DIR/$CFG_FILE"
}

######## STATUS COMMAND ########
handle_status_command() {
  validate_status_arguments "$1" "$2"
  status_command "$1" "$2"
}

validate_status_arguments() {
  if [ -z "$1" ]; then
    echo "Error: missing argument <cluster_name>"
    print_usage "$0"
    exit 1
  fi
}

status_command() {
  if [ -z "$2" ]; then
    echo "Node not specified, assuming all."
    for node in "$1"/*; do
      status_node "$node"
    done
  else
    status_node "$2"
  fi
}

status_node() {
  cd "$1" || exit 1
  echo -n "$1 "
  PROCESS=$(pgrep -a java | grep "$PWD" | grep -v grep)
  if [ -z "$PROCESS" ]; then
    echo "[NOT RUNNING]"
  else
    echo "[RUNNING]"
  fi
  cd - >/dev/null || exit 1
}

######## START COMMAND ########
handle_start_command() {
  validate_start_stop_arguments "$1" "$2"
  start_command "$1" "$2"
}

validate_start_stop_arguments() {
  if [ -z "$1" ]; then
    echo "Error: missing argument <cluster_name>"
    print_usage "$0"
    exit 1
  fi
}

start_command() {
  if [ -z "$2" ]; then
    echo "Node not specified, assuming all."
    for node in "$1"/*; do
      start_node "$node"
    done
  else
    start_node "$1/$2"
  fi
}

start_node() {
  cd "$1" || exit 1
  echo -n "STARTING $1..."
  EXECUTABLE=$(ls "$PWD"/bin/*.jar)
  CONSOLE_OUTPUT_FILE="$PWD/log/console.log"

  # Start process if not running already
  PROCESS=$(pgrep -a java | grep "$PWD" | grep -v grep)
  if [ -z "$PROCESS" ]; then
    nohup "$JAVA" -jar "$EXECUTABLE" >"$CONSOLE_OUTPUT_FILE" 2>&1 </dev/null &
  else
    echo " [ALREADY RUNNING]"
    cd - >/dev/null || exit 1
    return
  fi

  # Wait for process to start
  ITERATIONS=$((STARTUP_TIMEOUT * 10))
  for ((i = 0; i < ITERATIONS; i++)); do
    PROCESS=$(pgrep -a java | grep "$PWD" | grep -v grep)
    if [ -n "$PROCESS" ]; then
      echo " [OK]"
      break
    else
      echo -n "."
      sleep 0.1
    fi
  done
  if [ -z "$PROCESS" ]; then
    echo " [FAIL]"
  fi
  cd - >/dev/null || exit 1
}

######## STOP COMMAND ########
handle_stop_command() {
  validate_start_stop_arguments "$1" "$2"
  stop_command "$1" "$2"
}

stop_command() {
  if [ -z "$2" ]; then
    echo "Node not specified, assuming all."
    for node in "$1"/*; do
      stop_node "$node"
    done
  else
    stop_node "$1/$2"
  fi
}

stop_node() {
  cd "$1" || exit 1
  PROCESS=$(pgrep -a java | grep "$PWD" | grep -v grep)
  PID=$(echo "$PROCESS" | awk '{print $1}')

  echo -n "STOPPING $1... "
  if [ -z "$PID" ]; then
    echo "[FAIL]"
  else
    kill -s "$SIGNAL" "$PID"
    echo "[OK]"
  fi
  cd - >/dev/null || exit 1
}

######## ENTRY POINT ########
case $1 in
create)
  handle_create_command "$2" "$3"
  ;;
status)
  handle_status_command "$2" "$3" # TODO: handle more than one node name
  ;;
start)
  handle_start_command "$2" "$3" # TODO: handle more than one node name
  ;;
stop)
  handle_stop_command "$2" "$3" # TODO: handle more than one node name
  ;;
"") # no command at all
  echo "Error: missing command"
  print_usage "$0"
  ;;
*) # unknown command
  echo "Error: unknown command $1"
  print_usage "$0"
  ;;
esac
