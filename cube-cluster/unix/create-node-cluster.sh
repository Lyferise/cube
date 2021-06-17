#!/bin/bash

# This script is run with two arguments and will create a subdirectory from the
# current path containing cluster deployment configuration for N nodes. For example,
# the following command
#
# ./create-node-cluster.sh cube 3
#
# results in the following directory layout.
#
# cube
#  node1782
#    start-node.sh
#    stop-node.sh
#    conf
#      application.yml
#    bin
#      lyferise-1.666.jar
#    log
#    data
#  node2761
#    ...
#  node3982
#    ...
#
#
# Node IDs are assigned using the formula nodeId = 1782 + 913 * k

PORT_BASE=7000
NODE_PREFIX="node"
CFG_DIR="conf"
CFG_FILE="application.yml"

make_node_id() {
    newId=$((1782 + 913 * ($1-1)));
    echo "$newId";
}

make_node_dir_structure() {
    mkdir "$1";
    mkdir "$1/$CFG_DIR";
    mkdir "$1/bin";
    mkdir "$1/log";
    mkdir "$1/data";
}

copy_script() {
    cp ./start-node.sh "$1";
    cp ./stop-node.sh "$1";
}

copy_config() {
    cp "../$CFG_DIR/$CFG_FILE" "$1/$CFG_DIR";
}

modify_config() {
    CFG_PATH="$1/$CFG_DIR/$CFG_FILE"
    sed -i "s/nodeId: 1/nodeId: $2/g" "$CFG_PATH"
    sed -i "s/port: \([0-9]\+\)/port: $3/g" "$CFG_PATH"
}

copy_binary() {
    cp "../build/libs/"*".jar" "$1/bin";
}

add_cluster_info() {
    echo "$1/$2/$CFG_DIR/$CFG_FILE"
}

create_dir_structure() {
    mkdir "$1";
    declare -a nodeCfgs
    declare -a nodeIds
    declare -a nodePorts
    for i in $(seq 1 "$2")
    do
        nodeId=$(make_node_id "$i");
        nodeName="$NODE_PREFIX$nodeId";
        nodeDir="./$1/$nodeName"
        make_node_dir_structure "$nodeDir";
        copy_script "$nodeDir";
        copy_config "$nodeDir";
        modify_config "$nodeDir" "$nodeId" $(("$PORT_BASE"+"$i"))
        copy_binary "$nodeDir";
        nodeCfgs+=("$nodeDir/$CFG_DIR/$CFG_FILE");
        nodeIds+=("$nodeId");
        nodePorts+=($(("$PORT_BASE"+"$i")));
    done

    for dir in "${nodeCfgs[@]}"
    do
        echo "$dir"
        printf "cluster:\n" >> "$dir"
        for idx in $(seq 0 $((${#nodeIds[@]}-1)))
        do
            {
              printf "  - node:\n"
              printf "    nodeId: %s\n" "${nodeIds[$idx]}"
              printf "    address: ws://localhost:%s\n" "${nodePorts[$idx]}"
            } >> "$dir"
        done
    done
}

print_usage() {
    echo "Usage: $1 <name> <number_of_nodes>";
}

validate_arguments() {
    if [ -z "$1" ] | [ -z "$2" ]
    then
        echo "Error: missing argument(s)";
        print_usage "$0";
        exit 1;
    fi

    if ! [[ "$2" =~ ^[0-9]+$ ]]
    then
        echo "Error: non-integer argument";
        print_usage "$0";
        exit 1;
    fi
}

validate_arguments "$1" "$2"
create_dir_structure "$1" "$2"