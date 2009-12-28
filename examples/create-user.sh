#!/bin/bash
curl -v -H "Content-Type: application/json" --data-binary "{\"id\":\"$1\",\"password\":\"$2\"}" http://localhost:8080/users/