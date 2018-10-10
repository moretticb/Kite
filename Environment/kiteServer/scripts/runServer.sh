#!/bin/bash

cd ../data
java -cp ../jars/hsqldb.jar org.hsqldb.server.Server $1 $2 $3 $4 $5 $6 $7 $8 $9
