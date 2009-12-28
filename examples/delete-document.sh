#!/bin/bash
curl -v -u $1 -X DELETE "http://0.0.0.0:8080/users/$1/$2"