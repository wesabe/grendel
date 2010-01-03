#!/bin/bash
if [ $# -ne 1 ]; then
  echo "Show a user's documents:"
  echo "    $0 <id>"
  exit -1
fi
curl -v -u $1 "http://0.0.0.0:8080/users/$1/documents/" && echo