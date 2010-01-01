#!/bin/bash
if [ $# -ne 2 ]; then
  echo "Create a new user:"
  echo "    $0 <id> <password>"
  exit -1
fi
curl -v -H "Content-Type: application/json" --data-binary "{\"id\":\"$1\",\"password\":\"$2\"}" http://localhost:8080/users/