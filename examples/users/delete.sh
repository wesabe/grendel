#!/bin/bash
if [ $# -ne 1 ]; then
  echo "Delete a user:"
  echo "    $0 <id>"
  exit -1
fi
curl -v -u $1 -X DELETE "http://0.0.0.0:8080/users/$1"