#!/bin/bash
if [ $# -ne 3 ]; then
  echo "Store a document:"
  echo "    $0 <id> <document name> <document body>"
  exit -1
fi
curl -v -u $1 -X PUT -H "Content-Type: text/plain" --data-binary "$3" "http://0.0.0.0:8080/users/$1/documents/$2" && echo