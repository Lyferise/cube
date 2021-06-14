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