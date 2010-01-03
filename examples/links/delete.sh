#!/bin/bash
if [ $# -ne 3 ]; then
  echo "Remove a user from a document's links:"
  echo "    $0 <id> <document name> <other id>"
  exit -1
fi
curl -v -u $1 -X DELETE "http://0.0.0.0:8080/users/$1/documents/$2/links/$3" && echo