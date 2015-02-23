#!/bin/bash
#
# creates the python classes for our .proto
#

project_base="/Users/arun_malik/Documents/sjsu/cmpe275/core-netty-4.2/python"

rm ${project_base}/comm_pb2.py

protoc -I=../resources --python_out=./ ../resources/comm.proto 
