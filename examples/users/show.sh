#!/bin/bash
if [ $# -ne 1 ]; then
  echo "Get information about a user:"
  echo "    $0 <id>"
  exit -1
fi

curl -v "http://0.0.0.0:8080/users/$1" && echo