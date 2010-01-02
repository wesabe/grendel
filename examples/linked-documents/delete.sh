#!/bin/bash
if [ $# -ne 3 ]; then
  echo "Delete a linked document:"
  echo "    $0 <id> <other id> <document>"
  exit -1
fi
curl -v -u $1 -X DELETE "http://0.0.0.0:8080/users/$1/linked-documents/$2/$3"