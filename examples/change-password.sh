#!/bin/bash
curl -v -u $1 -X PUT -H "Content-Type: application/json" --data-binary "{\"password\":\"$2\"}" "http://0.0.0.0:8080/users/$1"