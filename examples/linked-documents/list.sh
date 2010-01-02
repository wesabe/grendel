#!/bin/bash
if [ $# -ne 1 ]; then
  echo "See a list of a user's linked documents:"
  echo "    $0 <id>"
  exit -1
fi
curl -v -u $1 "http://0.0.0.0:8080/users/$1/linked-documents/"