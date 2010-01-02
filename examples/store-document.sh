#!/bin/bash
curl -v -u $1 -X PUT -H "Content-Type: text/plain" --data-binary "$3" "http://0.0.0.0:8080/users/$1/documents/$2"