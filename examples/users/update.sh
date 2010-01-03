#!/bin/bash
if [ $# -ne 2 ]; then
  echo "Change a user's password:"
  echo "    $0 <id> <password>"
  exit -1
fi

curl -v -u $1 -X PUT -H "Content-Type: application/json" --data-binary "{\"password\":\"$2\"}" "http://0.0.0.0:8080/users/$1" && echo