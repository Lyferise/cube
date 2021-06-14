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

make_node_name() {
    newId=$((1782 + 913 * ($1-1)));
   echo "node$newId";
}

make_node_dir_structure() {
    mkdir "./$1/$2";
    mkdir "./$1/$2/conf";
    mkdir "./$1/$2/bin";
    mkdir "./$1/$2/log";
    mkdir "./$1/$2/data";
}

copy_scripts() {
    cp ./start-node.sh "./$1/$2";
    cp ./stop-node.sh "./$1/$2";
}

copy_configs() {
    cp "../conf/"* "./$1/$2/conf";
}

copy_binaries() {
    cp "../build/libs/"*".jar" "./$1/$2/bin";
}

create_dir_structure() {
    mkdir $1;
    for i in $(seq 1 $2)
    do
        nodeName=$(make_node_name $i);
        make_node_dir_structure $1 $nodeName;
        copy_scripts $1 $nodeName;
        copy_configs $1 $nodeName;
        copy_binaries $1 $nodeName;
    done
}

print_usage() {
    echo "Usage: $1 <name> <number_of_nodes>";
}

validate_arguments() {
    if [ -z "$1" ] | [ -z "$2" ]
    then
        echo "Error: missing argument(s)";
        print_usage $0;
        exit;
    fi

    if ! [[ "$2" =~ ^[0-9]+$ ]]
    then
        echo "Error: non-integer argument";
        print_usage $0;
        exit;
    fi
}

validate_arguments $1 $2
create_dir_structure $1 $2    

