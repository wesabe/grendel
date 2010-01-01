#!/bin/bash
if [ $# -ne 2 ]; then
  echo "Show a document:"
  echo "    $0 <id> <document>"
  exit -1
fi
curl -v -u $1 "http://0.0.0.0:8080/users/$1/documents/$2"